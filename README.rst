========================================================================
Zephyr for JIRA (Server | Data Center | Cloud versions) Tests Importer Utility
========================================================================


The Zephyr for JIRA Tests Importer Utility is a Scala+Java based program used in conjuction with the Zephyr for JIRA plugin to import Tests into JIRA from Excel and XML (coming soon) file formats.


Features
-------- 

- Import Tests from Excel (xls, xlsx) files into JIRA (5 and above).


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
- Create a mapping of the columns in the Excel file ( use Excel column IDs A,B,C,... ) to the appropriate fields in Zephyr for JIRA
- Click on the "Start Import" button.

Once the import is completed, the "Start Import" button will be re-enabled.  At this point, you can check your imported tests in the appropriate Zephyr for JIRA project.


Limitations
----------- 

- Import Tests from XML (TestLink) option not implemeted yet (coming soon)
- If your JIRA is using a self-signed SSL certificate make sure that the certificate is added to the Java keystore before launching the utility


Source
------

You can check out the source of this from `zfj-importer
<https://bitbucket.org/zfjdeveloper/zfj-importer/>`_ repo.


Compiling from Source
---------------------

1. Download and setup SBT (Simple-Build-Tool) as explained in https://github.com/harrah/xsbt/wiki/Getting-Started-Setup
2. Download the source for zfj-importer repo from https://bitbucket.org/zfjdeveloper/zfj-importer/src/
3. Open a command prompt and type **sbt assembly**. This will download appropriate binary dependencies and build the project. 

*This codebase can only compile with java 1.6*

# On Mac

```sbt -java-home /Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home assembly```


License
-------
This program follows the Apache License version 2.0 (<http://www.apache.org/licenses/> ) That means:

It allows you to:

*   freely download and use this software, in whole or in part, for personal, company internal, or commercial purposes; 
*   use this software in packages or distributions that you create. 

It forbids you to:

*   redistribute any piece of our originated software without proper attribution; 
*   use any marks owned by us in any way that might state or imply that we www.getzephyr.com endorse your distribution; 
*   use any marks owned by us in any way that might state or imply that you created this software in question. 

It requires you to:

*   include a copy of the license in any redistribution you may make that includes this software; 
*   provide clear attribution to us, www.getzephyr.com for any distributions that include this software 

It does not require you to:

*   include the source of this software itself, or of any modifications you may have 
    made to it, in any redistribution you may assemble that includes it; 
*   submit changes that you make to the software back to this software (though such feedback is encouraged). 

See License FAQ <http://www.apache.org/foundation/licence-FAQ.html> for more details.

Feedback
--------

Please ask your questions on `Atlassian Answers
<https://answers.atlassian.com/questions/topics/16646242/zephyr-importer>`_.

For feedback or code questions, send an email to developer@getzephyr.com


Have fun!