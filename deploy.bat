@echo off

REM %1 - environment [dev, test, prod, uat, sit]
REM %2 - items count [0 - no generation]

set PROJECT=har
set ENVIRONMENT=dev
set CF_SRC_BUCKET_NAME=%PROJECT%-cloudformation-source

echo ------------------------------------
echo --- S3 Bucket for CloudFormation ---
echo ------------------------------------
aws s3api create-bucket --bucket %CF_SRC_BUCKET_NAME%

echo -------------------
echo --- Permissions ---
echo -------------------
cd deployments/sam/permissions
call deploy.bat %PROJECT% %ENVIRONMENT% %CF_SRC_BUCKET_NAME%
cd ../../..

echo -------------------------
echo --- Shared Components ---
echo -------------------------
cd deployments/sam/shared
call deploy.bat %PROJECT% %ENVIRONMENT% %CF_SRC_BUCKET_NAME%
cd ../../..

echo ----------------------
echo --- function-users ---
echo ----------------------
cd function-users
call deploy.bat %PROJECT% %ENVIRONMENT% %CF_SRC_BUCKET_NAME%
cd ..

echo ----------------------------------
echo --- function-health-expert-one ---
echo ----------------------------------
cd function-health-expert-one
call deploy.bat %PROJECT% %ENVIRONMENT% %CF_SRC_BUCKET_NAME%
cd ..


