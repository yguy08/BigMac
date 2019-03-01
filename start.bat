@echo off

REM log4j2 config file location
SET log4j2_config=log4j2.xml

REM The 'fat' jar
SET jar=.\target\BigMac-0.0.1.jar

REM Start Class
SET start_class=com.bigmac.Application

START java -Xmx512M -Xms64M -Dlog4j.configurationFile=%log4j2_config% -cp %jar% %start_class%