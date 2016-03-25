#!/bin/bash

source config.sh

if [[ ! $# == "5" ]]; then
	echo "expecting 5 arguments, $# given" >&2
	exit
fi

direction="$1"
resDir="$2"
batches="$3"
metric="$4"
worker="$5"

inputDir=$resDir/$partDir/$workerDirPrefix$worker/
outputDir=$resDir/$compDir/$workerDirPrefix$worker/
logs=$resDir/$logDir


if [[ ! -d $logs ]]; then mkdir -p $logs; fi
start > $logs/worker${worker}.dat

java -jar $headless computation.jar $direction - $inputDir $graphFilename $batchSuffix $outputDir $batches $run $metric - $zipType $computationSleep $computationTimeoutAfter 1> $logs/worker${worker}.log 2> $logs/worker${worker}.err

echo "exit-value: $?" >> $logs/worker${worker}.dat
end >> $logs/worker${worker}.dat

if [[ $(wc -l $logs/worker${worker}.err) == "0" ]]; then
	rm $logs/worker${worker}.err
else
	cat $logs/worker${worker}.err >&2
fi