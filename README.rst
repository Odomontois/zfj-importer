======================================
Zephyr for JIRA Tests Importer Utility
======================================


The Zephyr for JIRA Tests Importer Utility is a Scala+Java based program used in conjuction with the Zephyr for JIRA plugin to import Tests into JIRA from Excel and XML (coming soon) file formats.


Features
-------- 

- Import Tests from Excel (xls, xlsx) files into JIRA 5.


Download
-------- 

You can download the latest pre-compiled binary from the `Downloads 
<https://bitbucket.org/zfjdeveloper/zfj-importer/downloads/>`_ tab.


Usage
----- 

The Zephyr for JIRA Tests Importer Utility is a java based program and so can run on Windows, MacOS etc..

To launch the utility simply double-click the JAR file, or execute the following CLI Java command: 

   *java -cp zfj-importer-utility-n.nn.jar com.thed.zfj.ui.ImportSwingApp* 

Then follow these steps:

- Select Excel (the XML option should work in the future)
- Type in:
	JIRA server URL or IP address in the following format: 
		http(s)://<jira server name or IP address>/rest
	Username and password: 
		This should be a user that has write privileges to the project into which tests are to be imported
		
	Click on the Connect button
	
- Once successfully connected, the app will display JIRA projects available

	Select the project into which you wish to import tests
	
	Select Issue type "test"
	
- Identify the Discriminator (this allows the import process to distinguish between multiple test cases in a single Excel file - possible discriminators are "By empty row", "By ID change", or "By Testcase name change")
- Pick the import file from the system where you are running the importer
- Create a mapping of the columns in the Excel file use column IDs (A,B,C,...) to the appropriate fields in Zephyr for JIRA
- Click on the "Start Import" button.

Once the import is completed, the "Start Import" button will be re-enabled.  At this point, you can check your imported tests in the appropriate Zephyr for JIRA project.


Limitations
----------- 

- Import Tests from XML (TestLink) option not implemeted yet (coming soon)
- If your JIRA is using a self-signed SSL certifiate make sure that the certifcate is added to the Java keystore before launching the utility


Source
------

You can check out the source of this from `zfj-importer
<https://bitbucket.org/zfjdeveloper/zfj-importer/>`_ repo.


Compiling from Source
---------------------

1. Download and setup SBT (Simple-Build-Tool) as explained in https://github.com/harrah/xsbt/wiki/Getting-Started-Setup
2. Download the source for zfj-importer repo from https://bitbucket.org/zfjdeveloper/zfj-importer/src/
3. Open a command prompt and type **sbt assembly**. This will download appropriate binary dependencies and build the project. 



Feedback
--------

Please email any questions, feedback or code contibutions to developer@getzephyr.com


Have fun!
