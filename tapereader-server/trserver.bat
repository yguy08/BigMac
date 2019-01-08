@echo off

SET target=.\target

REM log4j2 config file location
SET log4j2_config=log4j2.xml

REM The TR-server 'fat' jar
SET trserver_jar=tapereader-server-0.0.1.jar

REM Start Class
SET start_class=com.tapereader.ServerApplication

:start
SET START_TIME=%time%
ECHO Starting TR-server...

START "Ticker-Server - %START_TIME%" java -Xmx512M -Xms64M -Dlog4j.configurationFile=%log4j2_config% -cp %target%\%trserver_jar% %start_class%