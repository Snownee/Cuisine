@echo off
set modid=cuisine

setlocal enabledelayedexpansion

for %%x in (%*) do (

  echo Making %%x.json
  (
    echo {
    echo   "variants": {
    echo     "age=0": { "model": "%modid%:%%x_stage0" },
    echo     "age=1": { "model": "%modid%:%%x_stage0" },
    echo     "age=2": { "model": "%modid%:%%x_stage1" },
    echo     "age=3": { "model": "%modid%:%%x_stage1" },
    echo     "age=4": { "model": "%modid%:%%x_stage2" },
    echo     "age=5": { "model": "%modid%:%%x_stage2" },
    echo     "age=6": { "model": "%modid%:%%x_stage2" },
    echo     "age=7": { "model": "%modid%:%%x_stage3" }
    echo   }
    echo }
  ) > %%x.json

)