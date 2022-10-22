ORG    := yykhomenko
NAME   := hash-system
REPO   := ${ORG}/${NAME}
TAG    := $(shell git log -1 --pretty=format:"%h")
IMG    := ${REPO}:${TAG}
LATEST := ${REPO}:latest

build: ## Build version
	sbt stage

start: ## Start version
	sbt run

image: ## Build image
	docker build -t ${IMG} -t ${LATEST} .

publish: ## Publish image
	docker push ${REPO} --all-tags

pull: ## Pull image
	docker pull ${LATEST}

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-10s\033[0m %s\n", $$1, $$2}'
