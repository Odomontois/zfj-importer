package com.thed.zfj.ui

import java.awt.Dimension
import java.io.File
import java.util.{HashMap, LinkedHashMap, LinkedHashSet}
import javax.swing.table._
import javax.swing.text.DefaultCaret

import com.thed.model._
import com.thed.service.TestLinkImporterManagerImpl
import com.thed.service.impl.zie.{TestcaseImportManagerImpl, TestcaseWordImportManager, WordImportJob}
import com.thed.service.zie.ImportManager
import com.thed.util.{Discriminator, _}
import com.thed.zfj.model._
import com.thed.zfj.rest._
import dispatch.classic.StatusCode
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.LogFactory
import org.apache.commons.vfs.{AllFileSelector, FileObject, FileType, VFS}

import scala.actors.Actor
import scala.actors.threadpool.{Callable, Executors}
import scala.collection.JavaConversions._
import scala.swing.ListView._
import scala.swing._
import scala.swing.event._

object ImportSwingApp extends SimpleSwingApplication {

  val tfUrl = new TextField("http://localhost:2990/jira", 20)
  val btConnect = new Button { text = "Connect" }
  val tfUserName = new TextField("admin", 5);
  val tfPassword = new PasswordField("admin", 5);

  val chkbxIsJOD = new CheckBox{text="Cloud"; tooltip="select it if importing to JIRA On Demand?"}
  val tfZODUrl = new TextField{text = sys.env.get("ZFJURL").getOrElse("https://prod-api.zephyr4jiracloud.com/connect");
                                columns = 15; editable=false; tooltip="URL to access Zephyr For JIRA Cloud"};
  val tfAccessKey = new TextField("", 10);
  val tfSecretKey = new TextField("", 10);

  val importFileName = new TextField { columns = 22 }
  val importFileButton = new Button { text = "Pick Import File" }
  val importFolderButton = new Button { text = "Pick Import Folder" }
  val btImport = new Button { text = "Start Import"; horizontalAlignment = Alignment.Center; enabled = false }
  val chkbxFilesCleanup = new CheckBox{selected=true; tooltip="Cleanup previously imported files from success folder, no original files will be touched"}
  val status = new TextArea("") {
    editable = false;
  }
  // this ensures that the last message in the status is scrolled to
  status.peer.getCaret().asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)
  val cbProjects = new ComboBox(List[Project](JiraService.dummyProject)) {
    renderer = Renderer(_.name)
    enabled = false
  }
  val cbissueType = new ComboBox(List[IssueType](JiraService.dummyIssueType)) {
    renderer = Renderer(_.name)
    enabled = false
  }
  val tabs = new TabbedPane {
    pages += new TabbedPane.Page("Excel", ExcelImporter)
    pages += new TabbedPane.Page("XML", XMLImporter)
    if(StringUtils.equalsIgnoreCase(System.getProperty("importWord", "false"), "true"))
      pages += new TabbedPane.Page("Word", WordImporter)
  }

  def top = new MainFrame() {
    title = "Zephyr Testcase Importer: Pick Import Type"
    preferredSize = new Dimension(800, 800);

    contents = new SplitPane {

      leftComponent = new BoxPanel(Orientation.Vertical) {
        contents += new FlowPanel {
          contents += new Label("Url: ")
          contents += tfUrl
          //contents += new Label("JOD: ")
          contents += chkbxIsJOD
          contents += new Label("| username: ")
          contents += tfUserName
          contents += new Label("password: ")
          contents += tfPassword
          contents += btConnect
        }
        /*For JOD*/
        var zfjCloudFlowPenal = new FlowPanel(FlowPanel.Alignment.Left)(new Label("ZFJ URL: "), tfZODUrl, new Label("Access Key:"), tfAccessKey, new Label("Secret Key:"), tfSecretKey)
        zfjCloudFlowPenal.visible = false;
        contents += new FlowPanel(FlowPanel.Alignment.Left)(zfjCloudFlowPenal)

        /*Project/IssueType selection panel*/
        contents += new FlowPanel(FlowPanel.Alignment.Left)(
          new Label("Project: "), cbProjects, new Label("Issue Type: "), cbissueType)

        contents += new ScrollPane(tabs)

        contents += new FlowPanel(FlowPanel.Alignment.Left)(importFileName, importFileButton, importFolderButton, new Label("  |  "), new FlowPanel(FlowPanel.Alignment.Center)(chkbxFilesCleanup, btImport))

        listenTo(btConnect, cbProjects.selection, cbissueType.selection, tabs.selection, tfUrl, chkbxIsJOD)
        listenTo(btImport, importFileName, importFileButton, importFolderButton)

        reactions += {
          case ValueChanged(`tfUrl`) => {
            chkbxIsJOD.selected = StringUtils.containsIgnoreCase(tfUrl.text, "localhost") || StringUtils.containsIgnoreCase(tfUrl.text, "atlassian.net") || StringUtils.containsIgnoreCase(tfUrl.text, "jira.com");
            zfjCloudFlowPenal.visible = chkbxIsJOD.selected;
          }
          case ButtonClicked(`chkbxIsJOD`) => {
            zfjCloudFlowPenal.visible = chkbxIsJOD.selected;
          }
          case ButtonClicked(`btConnect`) => {
            JiraService.url_base = tfUrl.text
            JiraService.userName = tfUserName.text
            JiraService.passwd = tfPassword.password.mkString
            try {
              val res = JiraService.getProjects();
              cbProjects.peer.setModel(ComboBox.newConstantModel(res))
              cbProjects.enabled = true;
            } catch {
              case e: StatusCode => {
                cbProjects.enabled = false;
                Dialog.showMessage(this, "Unexpected response code: " + e.code, "Error fetching projects", Dialog.Message.Error)
              }
              case e: Exception => {
                cbProjects.enabled = false;
                Dialog.showMessage(this, e.getMessage(), "Error fetching projects", Dialog.Message.Error)
              }
            }
            enableImport
          }
          case SelectionChanged(`cbProjects`) => {
            println("Project changed " + cbProjects.selection.item.name)
            if (cbProjects.selection.item.id != "-1") {
              try {
                var issueTypes = JiraService.getMeta(cbProjects.selection.item.id).get(0).issuetypes
                issueTypes ::= JiraService.dummyIssueType
                cbissueType.peer.setModel(ComboBox.newConstantModel(issueTypes))
                cbissueType.enabled = true;
              } catch {
                case e: StatusCode => {
                  cbissueType.enabled = true;
                  Dialog.showMessage(this, "Unexpected response code: " + e.code, "Error fetching issue types", Dialog.Message.Error)
                }
                case e: Exception => {
                  cbissueType.enabled = true;
                  Dialog.showMessage(this, e.getMessage(), "Error fetching issue types", Dialog.Message.Error)
                }
              }
              enableImport
            } else {
              cbissueType.peer.setModel(ComboBox.newConstantModel(List[IssueType](JiraService.dummyIssueType)))
              cbissueType.enabled = false;
              enableImport
            }
          }
          case SelectionChanged(`cbissueType`) => {
            enableImport
          }
          case ButtonClicked(`importFileButton`) => {
            val configFileChooser = getSelectedImporter.getImportFileChooser()
            if (configFileChooser.showOpenDialog(null) == FileChooser.Result.Approve) {
              importFileName.text = configFileChooser.selectedFile.getAbsolutePath
              enableImport
            }
          }
          case ButtonClicked(`importFolderButton`) =>
            val configFileChooser = new FileChooser(new File("."))
            configFileChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

            if (configFileChooser.showOpenDialog(null) == FileChooser.Result.Approve) {
              importFileName.text = configFileChooser.selectedFile.getAbsolutePath
              enableImport
            }
          case EditDone(`importFileName`) => {
            enableImport()
          }
          case SelectionChanged(`tabs`) => {
            importFileName.text = ""
            importFileName.publish(new EditDone(importFileName))
          }
          case ButtonClicked(`btImport`) => {
            getSelectedImporter.importIssues
          }

        }
      }
      rightComponent = new ScrollPane(status)

      dividerLocation = preferredSize.height * 14 / 16
    }

    centerOnScreen()

    def enableImport() = {
      btImport.enabled = !importFileName.text.isEmpty() &&
        cbProjects.enabled && cbProjects.selection.item.id != "-1" &&
        cbissueType.enabled && cbissueType.selection.item.id != "-1"
    }
    
    
    
    
    
  }
  def getSelectedImporter(): BaseImporter = {
    tabs.selection.page.content.asInstanceOf[BaseImporter]
  }
  def createJobHistories() : LinkedHashSet[JobHistory] = {
    new LinkedHashSet[JobHistory]() {
      val log = LogFactory.getLog(this.getClass)
      case class Append(msg: String)
      object Appender extends Actor {
        def act() {
          loop {
            react {
              case Append(msg) =>
                log.debug(msg);
                status.append(msg);
                status.revalidate(); status.repaint()
            }
          }
        }
      }
      Appender.start()
      override def add(jb: JobHistory) = {
        val msg = jb.getActionDate + " " + jb.getComments + " \n"
        Appender ! Append(msg)
        super.add(jb);
      }
    }
  }
  abstract class BaseImporter extends FlowPanel {
    def setServerConfiguration():Unit = {
      JiraService.serverType = if(chkbxIsJOD.selected) ZfjServerType.Cloud else ZfjServerType.BTF;
      if(chkbxIsJOD.selected)
        JiraService.zConfig = new ZConfig.ZConfigBuilder().withJiraUserName(tfUserName.text).withJiraHostKey("N/A").withJIRABaseUrl(tfUrl.text).withJIRASharedSecret("N/A")
        .withZephyrBaseUrl(tfZODUrl.text).withZephyrAppKey("N/A").withZephyrAccessKey(tfAccessKey.text).withZephyrSecretKey(tfSecretKey.text).build
      else
        JiraService.zConfig = null;
    }
    def getImportFileChooser(): FileChooser
    def importIssues(): Unit ={
      setServerConfiguration();
      if(chkbxFilesCleanup.selected){

      }
      importIssuesInternal();
    }
    protected def importIssuesInternal()
  }

  object ExcelImporter extends BaseMappingImporter {
    override def getImportManager(): ImportManager = new TestcaseImportManagerImpl();
    override def getImportFileChooser(): FileChooser = {
      new FileChooser(new java.io.File(".")) {
        fileFilter = new javax.swing.filechooser.FileNameExtensionFilter(".xls and .xlsx files", "xls", "xlsx")
      }
    }

    override def getTableColumns: List[String] = {
      return List("JIRA Field", "Excel Column")
    }

    override def addBaseFields(): Unit = {
      super.addBaseFields();
      fieldConfigs.putAll(Constants.excelFieldConfigs)
    }
  }

  object XMLImporter extends BaseMappingImporter {
    excelFldPanel.visible = false
    override def getImportManager(): ImportManager = new TestLinkImporterManagerImpl();
    override def getImportFileChooser(): FileChooser = {
      new FileChooser(new java.io.File(".")) {
        fileFilter = new javax.swing.filechooser.FileNameExtensionFilter("Testlink .xml files", "xml")
      }
    }
    override def getTableColumns: List[String] = {
      return List("JIRA Field", "XML Column")
    }

    override def addBaseFields(): Unit = {
      super.addBaseFields();
      fieldConfigs.putAll(Constants.xmlFieldConfigs)
    }
  }
  object WordImporter extends BaseImporter {

    override def getImportFileChooser(): FileChooser = {
      new FileChooser(new java.io.File(".")) {
        fileFilter = new javax.swing.filechooser.FileNameExtensionFilter("Word .doc and .docx files", "doc", "docx")
      }
    }

    val components = new TextField { columns = 15 }
    val labels = new TextField { columns = 15 }
    contents += new BoxPanel(Orientation.Vertical) {
      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        new Label("Components: (comma separated list)"), components)

      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        new Label("Labels: (comma separated list)"), labels)
    }
    def importIssuesInternal() = {
      status.text = ""

      val jobHistories = createJobHistories()
      
      val importManager = new TestcaseWordImportManager();
      val importJob = new WordImportJob(jobHistories, importFileName.text, components.text, labels.text)
      JiraService.project = new Project(cbProjects.selection.item.id)
      JiraService.issueType = new IssueType(cbissueType.selection.item.id)
      val executor = Executors.newSingleThreadExecutor
      val result = executor.submit(new Callable {
        val log = LogFactory.getLog(this.getClass)
        def call = {

          importManager.importAllFiles(importJob)
          importJob.getHistory().toString();
        }
      })
      result.get()
    }
  }

  abstract class BaseMappingImporter extends BaseImporter {

   	val fieldConfigs : HashMap[String, FieldConfig] = new LinkedHashMap();

    val spreadSheet = new Spreadsheet(15, 2, getTableColumns)
    val chkAttachFile = new CheckBox()
    val cbDiscriminator = new ComboBox(List[Pair[Discriminator, String]](
      Pair(Discriminator.BY_SHEET, "By Sheet"),
      Pair(Discriminator.BY_EMPTY_ROW, "By Empty Row"),
      Pair(Discriminator.BY_ID_CHANGE, "By ID Change"),
      Pair(Discriminator.BY_TESTCASE_NAME_CHANGE, "By Testcase name Change"))) {
      renderer = Renderer(_._2)
    }
    val chkImportAllSheets = new CheckBox()
    val tfStartingRowNumber = new TextField("2", 5)
    val tfSheetFilter = new TextField(".*", 5)

    tfSheetFilter.enabled = false
    val excelFldPanel = new BoxPanel(Orientation.Vertical) {

      contents += new FlowPanel(FlowPanel.Alignment.Left)() {
        contents += new Label("Discriminator:")
        contents += cbDiscriminator
        contents += new Label("Starting Row #:")
        contents += tfStartingRowNumber
      }

      contents += new FlowPanel(FlowPanel.Alignment.Left)() {
        contents += new Label("Import all sheets:")
        contents += chkImportAllSheets
        contents += new Label("Sheet Filter:")
        contents += tfSheetFilter
      }
    }

    contents += new BoxPanel(Orientation.Vertical) {
      contents += excelFldPanel
      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        new Label("Attach worksheet to issue"), chkAttachFile)
      contents += spreadSheet
    }
    listenTo(spreadSheet, chkImportAllSheets, cbissueType.selection)
    reactions += {
      case ButtonClicked(`chkImportAllSheets`) =>
        tfSheetFilter.enabled = chkImportAllSheets.selected
      case SelectionChanged(`cbissueType`) => {
	        println("IssueType changed " + cbissueType.selection.item.name + cbProjects.selection.item.issuetypes)
	        if (cbissueType.selection.item.id != "-1") {
	          addBaseFields; addCustomFields
	          println("Fields populated " + fieldConfigs)
	          spreadSheet.tableModel.addAll(fieldConfigs)
	          println("New Fields should show up in Table ")
	          spreadSheet.table.revalidate()
	        }
    	}
    }

    /**
     * Adds customes field configurations to Constants, from where, they are accessed to populate UI as well to create customField data construct consumed to create Rest Request
     */
    def addCustomFields {
      cbissueType.selection.item.ensuring(_.fields != null).fields.foreach {
        entry =>
          val (fldName, fldVal) = entry;

          if (fldName.startsWith("customfield")) {
            val fieldMetadataId = populateFieldTypes(fldVal.asInstanceOf[Map[String, AnyRef]])
            //Map(required -> false, schema -> Map(type -> string, custom -> com.atlassian.jira.plugin.system.customfieldtypes:textfield, customId -> 10100), name -> P, operations -> List(set))
            var desc = fldVal.asInstanceOf[Map[String, String]].get("name").get.toString
            //val datatype = fldVal.asInstanceOf[Map[String, AnyRef]].get("schema").get.asInstanceOf[Map[String, AnyRef]].get("type").get.toString
            val required = (fldVal.asInstanceOf[Map[String, AnyRef]].get("required").get.asInstanceOf[Boolean])
            if (required) desc += " *"
            val allowedValuesArray = fldVal.asInstanceOf[Map[String, AnyRef]].get("allowedValues") match {
              case Some(valueMap) => valueMap.asInstanceOf[List[AnyRef]]
              case None => List()
            }
            if (!desc.contains("Zephyr"))
              fieldConfigs.put(fldName, new FieldConfig(fldName, "testcase", false, fieldMetadataId, fldName, fldName, desc, "This is " + desc, required, true, true, true, 255, allowedValuesArray))
          }
      }
    }

    /**
     * Creates FieldTypeMetadata and stores it in <code>#Constants</code>
     * Called every time when issueType Changes
     * @param fldVal
     * @return fieldMetaDataId which is concatenation of jiraDataType and itemDataType (only populated if jiraDataType is Array)
     */
    def populateFieldTypes(fldVal: Map[String, AnyRef]): String = {
      val customFldMetadata = fldVal.get("schema").get.asInstanceOf[Map[String, AnyRef]]

      val jiraDataType = customFldMetadata.get("type").get.toString
      val itemsDataType = {
        if (jiraDataType == "array" && customFldMetadata.contains("items")) {
          customFldMetadata.get("items").get.toString
        } else { "" }
      }
      val customType = customFldMetadata.get("custom").getOrElse("").toString;
      val fieldMetadataId: String = jiraDataType + ":" + itemsDataType + ":" + customType
      val fieldMetadata = new FieldTypeMetadata(fieldMetadataId, "Text (1024)", jiraDataType, itemsDataType, customType, 1024, true, 100)
      Constants.fieldTypeMetadataMap.put(fieldMetadataId, fieldMetadata)
      fieldMetadataId
    }

    def addBaseFields(): Unit = {
      fieldConfigs.clear()
    }

    override def importIssuesInternal() = {

      status.text = ""
      var fieldMapDetails = new java.util.HashSet[FieldMapDetail]();
      for (data <- spreadSheet.tableModel.rowData) {
        fieldMapDetails.add(new FieldMapDetail(data(2).asInstanceOf[String], data(1).asInstanceOf[String]))
      }

      val fieldMap = new FieldMap(tfStartingRowNumber.text.toInt, cbDiscriminator.selection.item._1, fieldMapDetails)
      val jobHistories = createJobHistories

      val importJob = new ImportJob(importFileName.text, fieldMap, fieldConfigs, jobHistories, Option(tfSheetFilter.text).filter(_ => chkImportAllSheets.selected))
      importJob.setAttachFile(chkAttachFile.selected)
      JiraService.project = new Project(cbProjects.selection.item.id)
      JiraService.issueType = new IssueType(cbissueType.selection.item.id)
      JiraService.serverType = if(chkbxIsJOD.selected) ZfjServerType.Cloud else ZfjServerType.BTF;
      if(chkbxIsJOD.selected)
        JiraService.zConfig = new ZConfig.ZConfigBuilder().withJiraUserName(tfUserName.text).withJiraHostKey("N/A").withJIRABaseUrl(tfUrl.text).withJIRASharedSecret("N/A")
          .withZephyrBaseUrl(tfZODUrl.text).withZephyrAppKey("N/A").withZephyrAccessKey(tfAccessKey.text).withZephyrSecretKey(tfSecretKey.text).build
      else
        JiraService.zConfig = null;
      cleanupSuccessFolder(importJob);
      val executor = Executors.newSingleThreadExecutor
      val result = executor.submit(new Callable {
        val log = LogFactory.getLog(this.getClass)
        def call = {
          JiraService.startImport(importJob, getImportManager())
        }
      })
      result.get()
    }

    def cleanupSuccessFolder(importJob: ImportJob): AnyVal = {
      if (chkbxFilesCleanup.selected) {
        val fileObj = VFS.getManager.resolveFile(importJob.getFolder)
        var successFolder: FileObject = null
        if (fileObj.getType == FileType.FOLDER) {
          successFolder = VFS.getManager.resolveFile(fileObj.toString + File.separator + "success");
        } else {
          successFolder = VFS.getManager.resolveFile(fileObj.getParent.toString + File.separator + "success");
        }
        if (successFolder.exists()) successFolder.delete(new AllFileSelector);
      }
    }

    def getTableColumns: List[String]
    def getImportManager(): ImportManager

  }

}

class Spreadsheet(val height: Int, val width: Int, tableColumns: List[String]) extends ScrollPane {
  val tableModel = new MyTableModel(Array[Array[Any]](), tableColumns)
  val table = new Table(height, width) {
    model = tableModel
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.LastColumn
    showGrid = true
    gridColor = new java.awt.Color(150, 150, 150)
  }
  //for ( (fieldId, field) <- Constants.fieldConfigs ) { println(field.getDisplayName()); tableModel.addRow( Array[AnyRef](field.getDisplayName(), "", fieldId) ) }

  val rowHeader = new ListView() {
    fixedCellWidth = 30
    fixedCellHeight = table.rowHeight
  }
  //for (row <- cells; cell <- row) listenTo(cell)
  viewportView = table
  rowHeaderView = rowHeader
}

class MyTableModel(var rowData: Array[Array[Any]], var columnNames: Seq[String]) extends AbstractTableModel {
  override def getColumnName(column: Int) = columnNames(column).toString
  def getRowCount() = rowData.length
  def getColumnCount() = columnNames.length
  def getValueAt(row: Int, col: Int): AnyRef = rowData(row)(col).asInstanceOf[AnyRef]
  override def isCellEditable(row: Int, column: Int) = (column == 1)
  override def setValueAt(value: Any, row: Int, col: Int) {
    rowData(row)(col) = value
  }
  def addRow(data: Array[AnyRef]) {
    rowData ++= Array(data.asInstanceOf[Array[Any]])
  }

  def addAll(data: HashMap[String, FieldConfig]) {
    rowData = Array()
    data.foreach { entry => val (fieldId, field) = entry; addRow(Array[AnyRef](field.getDisplayName(), "", fieldId)) }
  }
}
