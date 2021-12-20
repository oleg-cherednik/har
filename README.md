# Build project

```commandline
mvn clean verify
```
# Load initial data (if needed)

```commandline
python function-users/load_data.py
```

## Set up AWS Merck profile

Go to `~/.aws` directory and update `credentials` file:

```
aws_access_key_id=
aws_secret_access_key=
```

## Deployment

### Deploy or update AWS environment

```commandline
deploy.bat
```

# OpenAPI 3.0

rest_api.yaml
https://swagger.minnovi.com/
