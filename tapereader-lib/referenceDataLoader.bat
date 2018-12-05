@echo off

SET target=.\target

REM TR App Properties
SET profile=binance,poloniex,distributed

REM log4j2 config file location
SET log4j2_config=log4j2.xml

REM The TR-server 'fat' jar
SET trserver_jar=tapereader-lib-0.0.1.jar

REM Debug
SET tr_debug=-Xrunjdwp:transport=dt_socket,server=y,address=5555,suspend=n

REM Start Class
SET start_class=com.tapereader.util.ReferenceDataLoader

:start
SET START_TIME=%time%
ECHO Starting ReferenceDataLoader

START "TR-server - %START_TIME%" java -Xmx512M -Xms64M -Dtr.profile=%profile% -Dlog4j.configurationFile=%log4j2_config% -Dtr.properties=%tr_properties% %tr_debug% -cp %target%\%trserver_jar% %start_class%