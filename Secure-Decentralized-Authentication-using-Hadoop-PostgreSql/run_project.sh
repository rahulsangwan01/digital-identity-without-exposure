#!/bin/bash
echo "-------------------------------------------------------"
echo "🚀 STARTING RESEARCH DATA PIPELINE"
echo "-------------------------------------------------------"

# 1. CLEANUP PHASE
echo "[1/4] Cleaning previous analysis results..."
rm -rf analysis_output_*

# 2. EXECUTION PHASE (Automatic Error Testing)
echo "[2/4] Running Data Processor (Success Case)..."
java -cp "target/classes" com.loganalysis.DataProcessor 10 2

echo "[2.5/4] Running Data Processor (Triggering ERROR Case)..."
java -cp "target/classes" com.loganalysis.DataProcessor 10 0

# 3. AUTOMATION PHASE
echo "[3/4] Running Selenium Web Automation..."
mvn exec:java -Dexec.mainClass="com.loganalysis.AutomationApp"

# 4. BIG DATA ANALYSIS PHASE
echo "[4/4] Processing Consolidated Logs with Hadoop..."
# Ensure this command is on ONE line in your script:
java -cp "target/classes:$(mvn dependency:build-classpath | grep -v '\[INFO\]')" com.loganalysis.LogHadoopProcessor

# 5. FINAL REPORTING
echo "-------------------------------------------------------"
echo "📊 FINAL RESEARCH INSIGHTS:"
# This looks into the new timestamped folder created by Hadoop
cat analysis_output_*/part-r-00000
echo "-------------------------------------------------------"
echo "✅ Pipeline Complete! Logs stored in PostgreSQL & logs.txt"
