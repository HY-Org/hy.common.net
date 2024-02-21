

del /Q hy.common.net.jar
del /Q hy.common.net-sources.jar


call mvn clean package
cd .\target\classes


rd /s/q .\org\hy\common\net\junit

jar cvfm hy.common.net.jar META-INF/MANIFEST.MF META-INF org

copy hy.common.net.jar ..\..
del /q hy.common.net.jar
cd ..\..





cd .\src\main\java
xcopy /S ..\resources\* .
jar cvfm hy.common.net-sources.jar META-INF\MANIFEST.MF META-INF org 
copy hy.common.net-sources.jar ..\..\..
del /Q hy.common.net-sources.jar
rd /s/q META-INF
cd ..\..\..

pause