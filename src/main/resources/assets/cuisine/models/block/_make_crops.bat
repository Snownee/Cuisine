@echo off
set modid=cuisine

setlocal enabledelayedexpansion

for %%x in (%*) do (
  for %%n in (0,1,2,3) do (

    echo Making %%x_stage%%n.json
    (
      echo {
      echo     "parent": "%modid%:block/cross_crop",
      echo     "textures": {
      echo         "crop": "%modid%:block/%%x_stage_%%n"
      echo     }
      echo }
    ) > %%x_stage%%n.json

  )
)