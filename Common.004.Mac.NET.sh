#!/bin/sh

cd ./bin


rm -R ./org/hy/common/net/junit
rm -R ./org/hy/common/net/plugins

jar cvfm hy.common.net.jar MANIFEST.MF META-INF org

cp hy.common.net.jar ..
rm hy.common.net.jar
cd ..





cd ./src
jar cvfm hy.common.net-sources.jar MANIFEST.MF META-INF org 
cp hy.common.net-sources.jar ..
rm hy.common.net-sources.jar
cd ..
