#!/bin/sh

cd ./bin

rm -R ./org/hy/common/net/junit
rm -R ./org/hy/common/net/plugins

jar cvfm hy.common.net.jar MANIFEST.MF LICENSE org

cp hy.common.net.jar ..
rm hy.common.net.jar
cd ..

