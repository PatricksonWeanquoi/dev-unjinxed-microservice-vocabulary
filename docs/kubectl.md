# KUBECTL COMMANDS

## INIT CONFIG
#### DESCRIPTION: initialize the config
initialize<br>
`sudo kubeadm init --pod-network-cidr=<ip-address>/16 `
`mkdir -p ~/.kube && kubeadm init --pod-network-cidr=10.244.0.0/16 --ignore-preflight-errors=NumCPU && cp -i /etc/kubernetes/admin.conf $HOME/.kube/config && chown $(id -u):$(id -g) $HOME/.kube/config && kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/2140ac876ef134e0ed5af15c65e414cf26827915/Documentation/kube-flannel.yml && kubectl taint nodes --all node-role.kubernetes.io/master-`

## CONFIG
#### DESCRIPTION: kubectl remote cloud access config
Set access config file<br>
`export KUBECONCONFIG=<config-file.yaml>`


## POD
#### DESCRIPTION: wrapper object for running container image
Upload new pod to kubectl<br>
`kubectl apply -f <pod-file.yaml>`

Get Current uploaded pods<br>
`kubectl get pods `

Get detailed information about pod<br>
`kubectl describe pod <pod-name>`

Redirect traffic to pod<br>
`kubectl port-forward --address 0.0.0.0 <pod-name> 8080:8080`

Delete pod<br>
`kubectl delete pod <pod-name>`

watch pod status<br>
`kubectl get pods --watch`


## SERVICE
#### DESCRIPTION: object which connect traffics to pods
Upload new service to kubectl<br>
`kubectl apply -f <service-file.yaml>`

Get Current uploaded services<br>
`kubectl get services `

Get detailed information about service<br>
`kubectl describe pod <service-name>`

Redirect traffic to service<br>
`kubectl port-forward --address 0.0.0.0 service/<service-name> 8080:8080`

Delete Service<br>
`kubectl delete service <service-name>`

Scale Pod
`kubectl scale --replicas <count> deployment/<name-of0deployment>`


## DEPLOYMENT
#### DESCRIPTION: object orchestrates pods life-cycle
Upload new deployment to kubectl<br>
`kubectl apply -f <deployment-file.yaml>`

Get Current uploaded deployments<br>
`kubectl get deployments `

Get detailed information about deployment<br>
`kubectl describe deployment <deployment-name>`

Redirect traffic to service<br>
`kubectl port-forward --address 0.0.0.0 deployment/<deployment-name> 8080:8080`

Delete deployment<br>
`kubectl delete deployment <deployment-name>`

Scale Pod<br>
`kubectl scale --replicas <count> deployment/<name-of-deployment>`


## ROLLING DEPLOYMENT
#### DESCRIPTION: Monitor and check the rolling update
Monitor the progress<br>
`kubectl rollout status deployment <deploy-name>`


## LOGS
`kubectl logs --follow <pod-name>`

## COMMAND ON POD
Command<br>
`kubectl exec <pod-name> -- <command>`

Interactive Command Session<br>
`kubectl exec -it <pod-name> -- bash`

## KUBE DASHBOARD
`kubectl create -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.2.0/aio/deploy/recommended.yaml`

Start proxy<br>
`kubectl proxy`

To run it on the platform, run the following command<br>
`kubectl proxy --address 0.0.0.0 --accept-hosts='.*'`

`kubectl describe secret default-token`