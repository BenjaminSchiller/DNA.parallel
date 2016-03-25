#!/bin/bash

source config.sh

if [[ ! $# == "5" ]]; then
	echo "expecting 5 arguments, $# given" >&2
	exit
fi

resDir="$1"
workers="$2"
batches="$3"
partitionType="$4"
collation="$5"

metric="-"
dataset="-"

inputDirAux=$resDir/$auxDir/
inputDirWorker=$resDir/$compDir/${workerDirPrefix}PARTITION/
outputDir=$resDir/$collDir/
logs=$resDir/$logDir/

if [[ ! -d $logs ]]; then mkdir -p $logs; fi
start > $logs/collation.dat

java -jar $headless collation.jar $inputDirAux $inputDirWorker $outputDir $zipType $workers $batches $run $partitionType $collation - $collationSleep $collationTimeoutAfter $metric $dataset $collationPlotting 1> $logs/collation.log 2> $logs/collation.err

echo "exit-value: $?" >> $logs/collation.dat
end >> $logs/collation.dat

if [[ $(wc -l $logs/collation.err) == "0" ]]; then
	rm $logs/collation.err
else
	cat $logs/collation.err >&2
fi
