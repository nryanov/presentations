## Install (minikube)
### zalando postgres-operator
```shell
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator

kubectl create namespace postgres
helm install postgres-operator postgres-operator-charts/postgres-operator --version 1.14.0 --namespace postgres --wait
```

### postgres-debezium container
```shell
eval $(minikube docker-env)
make docker-build
```

## Run
```shell
# step 1  
# setup 3-node cluster
helm install postgres-debezium-environment ./postgres-cluster --namespace postgres
kubectl apply -n postgres -f ./manifests/minio.yaml

# step 2 -- init some tables in zalando DB:
kubectl apply -n postgres -f ./manifests/create-tables.yaml

# step 3 -- start cdc
kubectl apply -n postgres -f ./manifests/postgres-debezium.yaml

# step 4 (optional) -- forward master node port to be able to connect to it using localhost
kubectl port-forward -n postgres pod/$(kubectl get -n postgres pod -l spilo-role=master -o jsonpath='{.items[0].metadata.name}') 5432:55432
```

### Rollback / Uninstall
```shell
# rollback
helm rollback postgres-debezium-environment 1 --namespace postgres

# uninstall
kubectl delete -n postgres -f ./manifests/postgres-debezium.yaml
kubectl delete -n postgres -f ./manifests/create-tables.yaml
helm uninstall postgres-debezium-environment --namespace postgres 
```
