@echo off

set PROJECT=%1
set ENVIRONMENT=%2
set CF_SRC_BUCKET_NAME=%3

set MODULE=Permissions
set CF_STACK_NAME=%PROJECT%-%ENVIRONMENT%-%MODULE%
set S3_PREFIX=%ENVIRONMENT%/%MODULE%

sam deploy ^
        --region us-east-1 ^
        --stack-name %CF_STACK_NAME% ^
        --template-file template.yaml ^
        --s3-bucket %CF_SRC_BUCKET_NAME% ^
        --s3-prefix %S3_PREFIX% ^
        --capabilities CAPABILITY_NAMED_IAM ^
        --parameter-overrides ^
            ParameterKey=Project,ParameterValue=%PROJECT% ^
            ParameterKey=Environment,ParameterValue=%ENVIRONMENT%
