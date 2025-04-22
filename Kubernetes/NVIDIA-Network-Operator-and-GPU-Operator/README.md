# NVIDIA Network Operator and GPU Operator in Business Models

<table align="center">
  <tr>
    <td align="center">
      <img src="https://docs.nvidia.com/datacenter/cloud-native/gpu-operator/latest/_static/nvidia-logo-horiz-rgb-wht-for-screen.svg" alt="nvidia" width="200"/>
    </td>
  </tr>
</table>

## Introduction:

**NVIDIA Network Operator** and **NVIDIA GPU Operator** simplify the deployment of networking and GPU resources on Kubernetes clusters.

- **GPU Operator** automates the lifecycle management (driver installation, monitoring) for GPUs.
- **Network Operator** manages network plugins for NVIDIA network hardware (e.g., RoCE, DPDK) in Kubernetes.

Both are essential if you want **AI/ML**, **Deep Learning**, **High-Performance Computing (HPC)**, or **Data Processing** workloads to run smoothly inside Kubernetes!

## What is NVIDIA GPU Operator?

<table align="right">
  <tr>
    <td align="center">
      <img src="https://docs.nvidia.com/datacenter/cloud-native/gpu-operator/latest/_images/nvidia-gpu-operator-image.jpg" alt="nvida GPU operator" width="200"/>
    </td>
  </tr>
</table>

It automates:
- Installing **NVIDIA drivers**
- Setting up **Kubernetes Device Plugin** for GPU discovery
- Deploying **NVIDIA Container Toolkit** (to allow containers to use GPU)
- Monitoring GPUs via **DCGM Exporter** to Prometheus
- Health checking and reporting

## Key Features of NVIDIA GPU Operator

- 🔥 **Automated Driver Installation**  
- 📦 **GPU Sharing and Scheduling** (with MIG or time slicing)
- 🚀 **GPUDirect RDMA Support**
- 💾 **GPUDirect Storage Support**
- 📊 **GPU Monitoring and Alerts**
- 🛡️ **Secure Workload Isolation** (with MIG – Multi-Instance GPU)
- 🔗 **Dynamic GPU Resource Management**


---

# Business Model 1: Small-Scale Business (Startup, 10-50 employees)

### **Scenario:**  
A startup builds an AI-based video analytics platform.  
They have **4 NVIDIA A100 GPUs** in an on-prem Kubernetes cluster.

### **Goal**:
- Automate GPU driver and container setup.
- Enable fast communication (RoCEv2) between GPUs for AI video analytics workloads.

### **Required Tools**:
- **GPU Operator**: To automate GPU driver installation, Kubernetes device plugin, and runtime setup.
- **Network Operator**: For enabling **RoCEv2** (Remote Direct Memory Access) networking between GPUs for high-performance communication.

### **Challenge:**  
- Efficient GPU usage
- No time to manually configure drivers and networking

### **Step 1: Setup GPU Operator in Kubernetes**

1. **Add NVIDIA Helm Repository**:
   - This will allow you to install the GPU Operator through Helm (package manager for Kubernetes).
   
   ```bash
   helm repo add nvidia https://nvidia.github.io/k8s-device-plugin
   helm repo update
   ```

2. **Install GPU Operator**:
   - Run this command to install the GPU Operator into your cluster. It will manage NVIDIA drivers, the container toolkit, and GPU resources.

   ```bash
   helm install nvidia-gpu-operator nvidia/gpu-operator --namespace gpu-operator --create-namespace
   ```

3. **Verify Installation**:
   - Ensure the GPU Operator pods are running:
   
   ```bash
   kubectl get pods -n gpu-operator
   ```

4. **Check GPU Node Availability**:
   - To ensure that GPUs are being detected by Kubernetes, run the following command:
   
   ```bash
   kubectl describe node <your-node-name> | grep -i nvidia.com/gpu
   ```

5. **Testing GPU Access in Pods**:
   - Create a test pod that requests GPU resources:
   
   ```yaml
   apiVersion: v1
   kind: Pod
   metadata:
     name: test-gpu-pod
   spec:
     containers:
     - name: cuda-container
       image: nvidia/cuda:11.0-base
       resources:
         limits:
           nvidia.com/gpu: 1
   ```

   Apply the pod configuration:

   ```bash
   kubectl apply -f test-gpu-pod.yaml
   ```

   Check if the pod starts and uses the GPU:

   ```bash
   kubectl logs -f test-gpu-pod
   ```



### **Step 2: Install Network Operator for RoCEv2**

1. **Install the Network Operator**:
   - This step sets up the **RoCEv2 (Remote Direct Memory Access)** networking between GPUs on Kubernetes nodes.

   ```bash
   helm repo add nvidia-network https://nvidia.github.io/network-operator
   helm repo update
   helm install nvidia-network-operator nvidia-network/network-operator --namespace network-operator --create-namespace
   ```

2. **Enable RoCEv2 (for Intra-GPU communication)**:
   - You’ll need to configure the RoCEv2 network for high-speed GPU-to-GPU communication.
   
   Create a ConfigMap that enables **RoCEv2**:

   ```yaml
   apiVersion: v1
   kind: ConfigMap
   metadata:
     name: roce-config
     namespace: network-operator
   data:
     roce_enable: "true"
   ```

   Apply the configuration:

   ```bash
   kubectl apply -f roce-config.yaml
   ```

3. **Verify RoCEv2 Network**:
   - Check that **RoCEv2** is active across nodes using **`kubectl`**:

   ```bash
   kubectl get pods -n network-operator
   ```

   Check for high-throughput communication using **RoCEv2** for your AI workloads.



### **Step 3: Test and Monitor GPU Resource Utilization**

1. **Monitor GPU Utilization**:
   - Use **Prometheus** and **Grafana** (which can be set up using Helm) to track GPU usage, memory, and health.
   
   Install **Prometheus** via Helm:

   ```bash
   helm install prometheus stable/prometheus --namespace monitoring
   ```

   Install **Grafana** via Helm:

   ```bash
   helm install grafana stable/grafana --namespace monitoring
   ```

2. **Dashboard for GPU Monitoring**:
   - Create a custom **Grafana dashboard** for GPU metrics like memory utilization, temperature, power usage, etc.


### **Benefits:**
- 🚀  Fast deployment without deep GPU knowledge.

- 🧠 Better Resource Utilization:

- 🌱 Future-Proof Scalability:

---

# Business Model 2: Large-Scale Enterprise (Healthcare Company, AI Diagnostics)

### **Scenario:**  
A healthcare company wants to run **AI diagnostics** at global data centers.

### **Challenge:**
- Thousands of GPUs across Kubernetes clusters.
- Need GPUDirect Storage for low-latency data loading.
- Strict networking (DPDK, RoCE) for model training.

### **Goal**:
- Run AI diagnostics at scale across a global multi-cloud Kubernetes environment.
- Ensure fast training with GPUDirect RDMA and storage using **GPU Operator** and **Network Operator**.

### **Required Tools**:
- **GPU Operator**: Automates driver setup across multi-cloud regions.
- **Network Operator**: Enables **GPUDirect RDMA** for efficient GPU-to-GPU communication.
- **Prometheus/Grafana**: For monitoring GPU health and performance.
- **Helm**: For installing the GPU and Network Operators in a multi-cloud environment.

### **Implementation:**
- Centralize management of GPUs across multi-cloud Kubernetes clusters.
- Use **GPU Operator** to ensure consistent driver/runtime versions everywhere.
- Use **Network Operator** to offload networking and enable **GPUDirect RDMA** for cluster-wide training.
- Integrate with **Prometheus/Grafana** to monitor GPU health, errors, and usage.

### **Step 1: Setup GPU Operator in Multi-Cloud Kubernetes Cluster**

1. **Install GPU Operator** (same as small-scale):

   ```bash
   helm install nvidia-gpu-operator nvidia/gpu-operator --namespace gpu-operator --create-namespace
   ```

2. **Ensure Multi-cloud GPU Access**:
   - For global deployments, ensure **each region** has the same GPU setup. You can use **Helm** to deploy the GPU Operator on each cluster in your regions.

3. **Check Global GPU Resources**:
   - Across the global cluster, verify that GPUs are detected and accessible.

   ```bash
   kubectl describe node <global-node-name> | grep -i nvidia.com/gpu
   ```

### **Step 2: Install Network Operator for GPUDirect RDMA**

1. **Install Network Operator (as in small-scale)**:

   ```bash
   helm install nvidia-network-operator nvidia-network/network-operator --namespace network-operator --create-namespace
   ```

2. **Enable GPUDirect RDMA**:
   - For low-latency data transfer between GPUs and storage in multi-cloud deployments, enable **GPUDirect RDMA**:

   ```yaml
   apiVersion: v1
   kind: ConfigMap
   metadata:
     name: rdma-config
     namespace: network-operator
   data:
     rdma_enable: "true"
   ```

   Apply this configuration across your global clusters:

   ```bash
   kubectl apply -f rdma-config.yaml
   ```

3. **Verify GPUDirect RDMA**:
   - Ensure **GPUDirect RDMA** is enabled and working for the connected GPUs. Use **kubectl logs** to verify performance and check for errors.

### **Step 3: Use GPUDirect Storage for Optimized Data Loading**

1. **Configure GPUDirect Storage**:
   - Set up **GPUDirect Storage** for efficient data transfer from storage devices directly to GPUs.

   ```yaml
   apiVersion: v1
   kind: ConfigMap
   metadata:
     name: gpu-storage-config
     namespace: storage-operator
   data:
     gpudirect_storage_enable: "true"
   ```

2. **Mount Persistent Storage**:
   - Link **GPUDirect Storage** to your storage infrastructure (e.g., NVMe drives or high-speed SAN) to ensure that data is efficiently loaded into GPUs.

### **Step 4: Set Up Prometheus and Grafana for Multi-cloud GPU Monitoring**

1. **Install Prometheus** (as in small-scale):

   ```bash
   helm install prometheus stable/prometheus --namespace monitoring
   ```

2. **Install Grafana** (as in small-scale):

   ```bash
   helm install grafana stable/grafana --namespace monitoring
   ```

3. **Set Up Global Monitoring Dashboards**:
   - Create a **multi-cloud** GPU monitoring dashboard to track:
     - GPU health and status
     - Memory and compute utilization
     - Latency and throughput across regions

### **Benefits:**
- 🚀 Accelerated Training: Faster AI model convergence using RDMA and GPUDirect.

- 🔒 Unified Management: Standardized GPU configuration and monitoring globally.

- 📈 High Performance: Eliminate storage bottlenecks with direct GPU access.

- ⚡ Reduced Latency: RoCEv2 networking minimizes data movement delays.

- 🛡️ Improved Reliability: Central monitoring identifies failures early.

---

# *Key Concepts*

### 1. GPU Concurrency
- Kubernetes can run **multiple pods sharing a GPU** (via MIG or MPS).
- Example: Serve multiple ML models on a single A100 GPU.

### 2. GPUDirect RDMA (Remote Direct Memory Access)
- Direct transfer of data between network cards and GPU memory (no CPU bottleneck).
- **Useful for distributed deep learning** (Horovod, TensorFlow multi-node training).

### 3. GPUDirect Storage
- GPUs **read data directly from NVMe SSDs** or Storage Servers.
- No CPU mediation → reduces latency → ideal for large AI datasets (medical imaging, simulation).

<div style="display: flex; justify-content: center; gap: 20px;">
  <div style="border: 1px solid #ccc; border-radius: 10px; padding: 10px; width: 200px; text-align: center;">
    <img src="https://d2908q01vomqb2.cloudfront.net/fe2ef495a1152561572949784c16bf23abb28057/2023/09/12/GPU-Concurrency.png" alt="vGPU" style="width: 100%; height: auto; border-radius: 5px;">
  </div>
</div>


# 🧠 GPU Sharing Strategies in Kubernetes (NVIDIA Operators)

---

## 1. **Multi-Process Service (MPS)**

### What it is:  
MPS allows **multiple CUDA applications** to **run concurrently** on the **same GPU** by sharing GPU resources at the thread/process level.  
Instead of having one big application fully occupying a GPU, small apps can share it efficiently.

### Example:
A small startup has a Kubernetes cluster with **4 NVIDIA A100 GPUs**.  
They deploy 8 microservices doing real-time image processing, but **each only uses 25% GPU**.  

Without MPS → 1 microservice wastes almost an entire GPU.  
With MPS → multiple microservices can run simultaneously on the same GPU!

### Implementation:
**Steps to enable MPS with GPU Operator:**
1. Ensure you have installed the **NVIDIA GPU Operator** (latest version supports MPS automatically).
2. Modify the **ClusterPolicy** resource:
    - Enable `mps` in device plugin settings:
    ```yaml
    spec:
      devicePlugin:
        config:
          mps:
            enabled: true
    ```
3. Deploy your pods normally. MPS will allow multiple pods to share the same GPU at the CUDA stream level.

4. (Optional) Fine-tune `CUDA_MPS_ACTIVE_THREAD_PERCENTAGE` environment variable inside the container to control how much GPU each app can use.

### Cost Optimization:
✅ Avoids reserving **one full GPU** for tiny apps.  
✅ Boosts GPU usage to **80-90%** instead of ~10-20%.  
✅ Reduces **hardware needs** → fewer GPUs = less upfront and operational cost.

---

## 2. **Time Slicing**

### What it is:  
Time slicing splits the **GPU into virtual time slots** — each application gets a “slice” of time to access the full GPU resources.

Good when **latency is acceptable**, and you want **strong isolation** between apps.

### Example:
In a healthcare company, multiple AI models (cancer detection, anomaly scanning, image enhancement) need GPU compute but **don't need it continuously**.

Rather than buying **30 GPUs** for 30 models → you time-slice **10 GPUs** across all models.

### Implementation:
**How time slicing happens:**
- **Device Plugin Settings:** Configure the `timeSlicing` mode in NVIDIA's Kubernetes Device Plugin.
- Enable via **ClusterPolicy** or pod annotations:
```yaml
apiVersion: v1
kind: Pod
metadata:
  annotations:
    nvidia.com/mig.strategy: mixed
    nvidia.com/gpu.deploy.time-slicing: "true"
spec:
  containers:
  ...
```
- The operator will manage internal GPU context switching.


### Cost Optimization:
✅ One GPU can serve **many users**, **lowering capex** dramatically.  
✅ Prevents idling — only apps that *need* GPU get temporary access.

---

## 3. **Multi-Instance GPU (MIG)**

### What it is:  
MIG (only supported on A100, H100, A30, A40 GPUs) **physically partitions** one GPU into **multiple hardware-backed instances**.  
Each instance behaves like a smaller, isolated GPU!

Perfect for **workload isolation + guaranteed performance**.

### Example:
A SaaS platform runs customer-specific AI inference models.  
Instead of dedicating an entire A100 GPU for each customer, the company uses **MIG to carve out 7 separate GPU slices**, each assigned to different customer apps.

### Implementation:
**Steps to enable MIG in Kubernetes with GPU Operator:**
1. In **ClusterPolicy**, set MIG enabled:
```yaml
spec:
  mig:
    strategy: single
```
2. The operator automatically partitions the GPU.
3. Pods can request specific MIG devices using Kubernetes extended resources:
```yaml
resources:
  limits:
    nvidia.com/mig-1g.5gb: 1
```
(meaning 1 slice with 5 GB GPU memory)

### Cost Optimization:
✅ Maximize hardware investment — run **7 apps on one A100** instead of buying 7 separate GPUs!  
✅ Strong performance isolation — customers can't interfere with each other.

---

## 4. **Virtualization with vGPU (NVIDIA vGPU)**

### What it is:  
**NVIDIA vGPU** lets you **virtualize** a GPU like you virtualize a CPU with VMware or KVM.  
Different VMs or containers can share GPU slices with **driver-level isolation**.

**Great for very large enterprises** needing VDI (Virtual Desktop Infrastructure), or AI workloads with strict multi-tenancy needs.

### Example:
A global healthcare company deploys a Kubernetes cluster across 3 data centers.  
Each team (radiology, oncology, research) gets their own **vGPU-backed VMs** inside the same physical server to run AI models safely and independently.

### Implementation:
**Steps for vGPU setup:**
1. Use GPUs that support **vGPU licensing** (A100, A40, RTX 6000 Ada, etc.)
2. Install **NVIDIA vGPU software** and configure licensing.
3. In Kubernetes:
   - Use the **NVIDIA GPU Operator with vGPU driver mode**.
   - Set up scheduling for vGPU types (example below):
```yaml
resources:
  limits:
    nvidia.com/vgpu-devices: 1
```
4. Kubernetes + KubeVirt or VMware Tanzu manages GPU-backed VM deployments.

### Cost Optimization:
✅ **Dynamic sharing:** multiple VMs run on one GPU without physical overprovisioning.  
✅ **Compliance and tenant separation** for regulated industries (finance, healthcare).  
✅ **Scale up/down on demand** without buying dedicated GPU per team.

---

# 🚀 In Summary (Quick Table):

| Strategy | Best Use Case | Example | Cost Savings |
|:---|:---|:---|:---|
| MPS | Many small CUDA jobs | Multiple AI microservices | 4x GPU efficiency |
| Time Slicing | Non-real-time apps | AI model batch processing | Avoid idle GPUs |
| MIG | Strong isolation + steady workloads | Multi-tenant SaaS inference | Hardware ROI 7x |
| vGPU | Multi-tenant / VDI | Healthcare team separation | Dynamic GPU allocation |
