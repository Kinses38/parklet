#Pixel2 28, 29
#NexusLowRes 27, 28, 29
#

gcloud beta firebase test android run \
  --type robo \
  --app /home/ark/Documents/fyp/2020-ca400-kinses38-purcem23/src/app/build/outputs/apk/debug/app-debug.apk \
  --robo-script ./parklet_robo_script.json \
  --device model=Pixel2,version=29,locale=en,orientation=portrait  \
  --device model=Pixel2,version=28,locale=en,orientation=portrait  \
  --device model=NexusLowRes,version=29,locale=en,orientation=portrait  \
  --device model=NexusLowRes,version=28,locale=en,orientation=portrait  \
  --timeout 5m \
  --auto-google-login