# Case Study: Deploying DeepSeek R1 Distill Llama on Amazon Bedrock

<table align="center">
  <tr>
    <td align="center">
      <img src="https://registry.npmmirror.com/@lobehub/icons-static-png/latest/files/dark/bedrock-color.png" alt="ECR" width="200"/>
    </td>
    <td align="center" style="font-size: 40px;">+</td>
    <td align="center">
      <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/DeepSeek_logo.svg/512px-DeepSeek_logo.svg.png" alt="S3" width="300"/>
    </td>
  </tr>
</table>


## Overview

With the growing demand for powerful and cost-efficient large language models, **DeepSeek R1 Distill Llama** offers a compelling choice for enterprises looking to deploy high-performance AI models. This case study outlines the successful deployment of the **DeepSeek R1 Distill Llama-8B** model on **Amazon Bedrock**, showcasing how to harness serverless infrastructure for scalable inference without managing the underlying infrastructure.

---

## Objective

To **deploy and serve the DeepSeek R1 Distill Llama model** via **Amazon Bedrock**, utilizing a smooth, secure, and scalable approach suitable for production-grade AI applications.

---

## 🧰 Prerequisites

### Model Compatibility

Before deployment, ensure the model architecture is supported by Amazon Bedrock:

- Llama 2 
- Llama 3
- Llama 3.1
- Llama 3.2
- Llama 3.3

> ✅ **DeepSeek R1 Distill** is compatible, as it builds on the Llama family.

### Model Files Required

All files must follow the Hugging Face format and be available in an **S3 bucket**:

- Model weights in `.safetensors`
- `config.json`
- `tokenizer_config.json`, `tokenizer.json`, `tokenizer.model`

---

## Step-by-Step Deployment Guide

### 1️⃣ Install Dependencies

Begin by installing the necessary Python libraries:

```bash
pip install huggingface_hub boto3
```

---

### 2️⃣ Download the Model

Leverage Hugging Face to retrieve the model locally:

```python
from huggingface_hub import snapshot_download

model_id = "deepseek-ai/DeepSeek-R1-Distill-Llama-8B"
local_dir = snapshot_download(repo_id=model_id, local_dir="DeepSeek-R1-Distill-Llama-8B")
```

---

### 3️⃣ Upload to Amazon S3

Next, push the model to your Amazon S3 bucket:

```python
import boto3
import os

s3_client = boto3.client('s3', region_name='us-east-1')
bucket_name = 'your-s3-bucket-name'
local_directory = 'DeepSeek-R1-Distill-Llama-8B'

for root, dirs, files in os.walk(local_directory):
    for file in files:
        local_path = os.path.join(root, file)
        s3_key = os.path.relpath(local_path, local_directory)
        s3_client.upload_file(local_path, bucket_name, s3_key)
```

> 🔒 Ensure the S3 bucket is in the **same region** as your Bedrock deployment and properly accessible.

---

### 4️⃣ Import the Model into Amazon Bedrock

Now, move to the **Amazon Bedrock console**:

- Navigate to **Custom models → Import model**.
- Provide the **S3 URI**, e.g., `s3://your-s3-bucket-name/DeepSeek-R1-Distill-Llama-8B/`.
- Complete the guided import process.

> 📚 Refer to AWS’s [Bedrock custom model import documentation](https://docs.aws.amazon.com/bedrock/latest/userguide/custom-models.html) for details.

---

### 5️⃣ Invoke the Model via API

Once the model is active, invoke it through the Bedrock runtime:

```python
import boto3
import json

client = boto3.client('bedrock-runtime', region_name='us-east-1')
model_id = 'arn:aws:bedrock:us-east-1:your-account-id:imported-model/your-model-id'
prompt = "Your input prompt here"

response = client.invoke_model(
    modelId=model_id,
    body=json.dumps({'prompt': prompt}),
    accept='application/json',
    contentType='application/json'
)

result = json.loads(response['body'].read().decode('utf-8'))
print(result)
```

> 🛠 Replace `your-account-id` and `your-model-id` with your actual identifiers.

---

## 🧾 Outcome & Impact

- ✅ **Seamless Deployment**: The DeepSeek R1 Distill model was successfully imported and deployed using Amazon Bedrock.
- **Scalable Inference**: Inference workloads were offloaded to Bedrock's fully managed infrastructure, enabling horizontal scaling.
- **Secure Access**: Integration with AWS IAM and private S3 ensured data security and access control.

---

## Conclusion

Deploying the **DeepSeek R1 Distill Llama-8B** model on **Amazon Bedrock** demonstrates the ease of operationalizing state-of-the-art language models at scale, without managing infrastructure. This workflow opens the door to powerful AI-powered applications with minimal DevOps overhead.

> **Amazon Bedrock + DeepSeek R1** = Efficient, Scalable, and Production-Ready AI
