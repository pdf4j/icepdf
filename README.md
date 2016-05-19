# PDF4J ICEPDF

Fork of icepdf version 6.1.1 version with internal re-org and enhancements.

Part of re-org project has been moved to maven project structure. New modules introduced for better management and reuse.

### Project Modules

* _**core**_ - Main ICEPDF core API with enhancements, it is pure java implementation to view Acrobat PDF files.  
* _**print**_ - Simple Java API to print PDF from Java applications. For those who want 
use `levigo jbig2` needs to manually build using gpl profile.
* _**viewer**_ - Java Swing based application to view Acrobat PDF files.
* _**viewerapp**_ - A bundled application of viewer, self contained jar with all dependent 
jars included. For those who want use `levigo jbig2` needs to manually build using gpl profile.
* _**examples**_ - Some of java examples to use ICEPDF library
* _**webviewe**r_ - A JSF 2.x based web application which uses ICEPDF library to view and transform.

***

## CORE API

ICEPDF API, as is from SVN repository. Additional support classes are added to get clean compile of the project.

#### Maven dependency

```xml
<dependency>  
    <groupId>org.pdf4j.icepdf</groupId>  
    <artifactId>pdf4j-icepdf-core</artifactId>  
    <version>6.1.1</version>  
</dependency>  
```
***

## PDF4J ICEPDF Print API

Simple Java POJO classes provides the ability to print support form java applications.

#### Maven dependency

```xml
<dependency>  
    <groupId>org.pdf4j.icepdf</groupId>  
    <artifactId>pdf4j-icepdf-print</artifactId>  
    <version>6.1.1</version>  
</dependency>  
```

#### Console Printing

Use command line to print PDF files silently.

For projects which cannot use GPL based library can use the Apache license version

```
java -jar pdf4j-icepdf-print-6.1.1-asl.jar -file "PDF File name" -printer "Printer Name"
```

For those who can use GPL, use GPL version. Make a note, this require manual build to activate gpl profile `-Pgpl`.
```
java -jar pdf4j-icepdf-print-6.1.1-gpl.jar -file "PDF File name" -printer "Printer Name"
```
```
Parameter details:
        -file     "pdf file name", required field
        -printer  "printer name", Optional default printer used when ignored
        -help      Print this usage help.
```

#### PDF4J Print API

This PDF4J ICEPDF Print API provide limited features for silent printing. 

For more advanced print features and control, use PDF4J Print API which gives better print support and control.

***

## PDF Java Viewer

ICEPDF Java Viewer, as is from SVN repository. 

#### Maven dependency

```xml
<dependency>  
    <groupId>org.pdf4j.icepdf</groupId>  
    <artifactId>pdf4j-icepdf-viewer</artifactId>  
    <version>6.1.1</version>  
</dependency>  
```
#### Using Standalone Java Swing Viewer 

Standalone PDF Viewer, with self contained classes in a single jar file. Use manual build to create GPL jar file.

```
java -jar pdf4j-icepdf-viewerapp-asl.jar
```
or

```
java -jar pdf4j-icepdf-viewerapp-gpl.jar
```
***

## Manual Build for GPL use

For those who want use levigo jbig2 needs to manually build using gpl profile. Currently `PRINT` and `VIEWER` modules support these profile.

```
cd print 
or 
cd viewerapp

mvn clean package -Pgpl
```

***

## License

All contributions and work performed by PDF4J Team is applicable under ASL 2.0

***
