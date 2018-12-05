@echo off

SET target=.\target

REM TR App Properties
SET tr_properties=src\main\resources\application.properties

REM log4j2 config file location
SET log4j2_config=log4j2.xml

REM The TR-server 'fat' jar
SET tr_jar=tapereader-swing-ui-0.0.1.jar

REM Debug
SET tr_debug=-Xrunjdwp:transport=dt_socket,server=y,address=5556,suspend=y

REM Start Class
SET start_class=com.tapereader.gui.AppLoader

:start
SET START_TIME=%time%
ECHO Starting TR-Swing App...

START "TR-Swing - %START_TIME%" java -Xmx512M -Xms64M -Dlog4j.configurationFile=%log4j2_config% -Dtr.properties=%tr_properties% %tr_debug% -cp %target%\%tr_jar% %start_class%