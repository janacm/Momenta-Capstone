MANIFEST_FILE=${HOME}'/'${CIRCLE_PROJECT_REPONAME}'/app/src/mainAndroidManifest.xml'
VERSIONCODE=`grep versionCode $MANIFEST_FILE | sed 's/.*versionCode="//;s/".*//'`
VERSIONNAME=`grep versionName $MANIFEST_FILE | sed 's/.*versionName="//;s/\.[0-9]*".*//'`
NEWCODE=$BUILD_NUMBER
NEWNAME=$VERSIONNAME.$BUILD_NUMBER
echo "Updating Android build information. New version code: $NEWCODE - New version name: $NEWNAME";
sed -i '' 's/versionCode = *"'$VERSIONCODE'"/versionCode="'$NEWCODE'"/; s/versionName *= *"[^"]"/versionName="'$NEWNAME'"/' $MANIFEST_FILE