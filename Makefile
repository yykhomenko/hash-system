ORG    := yykhomenko
NAME   := hash-system
REPO   := ${ORG}/${NAME}
TAG    := $(shell git log -1 --pretty=format:"%h")
IMG    := ${REPO}:${TAG}
LATEST := ${REPO}:latest

build: ## Build a version
	sbt stage

run: ## Run a version
	sbt run

image: ## Build an image
	docker build -t ${IMG} -t ${LATEST} .

publish: ## Publish an image
	docker push ${REPO} --all-tags

pull: ## Pull an image
	docker pull ${LATEST}

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-17s\033[0m %s\n", $$1, $$2}'
