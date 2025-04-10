# **Case Study: Automating AWS ECR Image Backup to S3**

<table align="center">
  <tr>
    <td align="center">
      <img src="https://icon.icepanel.io/AWS/svg/Containers/Elastic-Container-Registry.svg" alt="ECR" width="200"/>
    </td>
    <td align="center" style="font-size: 40px;">+</td>
    <td align="center">
      <img src="https://cdn.worldvectorlogo.com/logos/amazon-s3-simple-storage-service.svg" alt="S3" width="200"/>
    </td>
  </tr>
</table>


## **Overview**

In modern DevOps practices, container images stored in AWS Elastic Container Registry (ECR) are critical assets. However, AWS does not natively support backup and version control for ECR images. This case study demonstrates a custom shell script-based solution to automate the **backup of ECR container images to Amazon S3**, enabling enhanced image durability, auditability, and disaster recovery.

---

## **Objective**

To create a fully automated process that:
- Authenticates to AWS ECR
- Pulls container images from multiple ECR repositories
- Saves them as `.tar` archives
- Uploads these tarballs to a structured directory path in an Amazon S3 bucket
- Optionally removes local images to manage disk space

---

## **Architecture Diagram**

```
ECR (Repo1, Repo2, ...) 
     │
     ▼
Docker (Pull Images)
     │
     ▼
Docker Save (→ .tar files)
     │
     ▼
S3 Bucket (Organized by Region / Repo / Tag)
```

---

## **Script Breakdown**

### **1. Environment Variables**

```bash
AWS_REGION="ap-southeast-1"
S3_BUCKET_NAME="your-backup-bucket"
REPOSITORIES="repo1 repo2"
AWS_ACCOUNT_ID="123456789012"
REGION_NAME="singapore"
```

These variables make the script dynamic and reusable across different accounts, regions, and projects.

---

### **2. Docker Authentication**

```bash
aws ecr get-login-password --region $AWS_REGION | docker login ...
```

This step ensures secure access to private ECR repositories.

---

### **3. Processing Repositories and Tags**

```bash
for REPO in $REPOSITORIES; do
  ...
  IMAGE_TAGS=$(aws ecr list-images ...)
```

The script retrieves image tags from each repository to process all available versions.

---

### **4. Image Backup Workflow**

For each image:
- **Pull** the image locally via Docker
- **Save** it to a tar archive
- **Upload** the tar to a structured path in S3
- **Clean up** local disk to save space

```bash
docker pull ...
docker save -o ...
aws s3 cp ...
rm -rf ...
docker rmi ...
```

---

## **S3 Path Structure**

To allow easy traceability and restoration, the S3 path follows this pattern:

```
<REGION_NAME>/<REPOSITORY_PATH>/<TAG>.tar
```

Example:
```
singapore/backend/app/2025-04-10.tar
```

---

## **Benefits**

| Feature                     | Benefit                                        |
|----------------------------|------------------------------------------------|
| Automated backups          | No manual intervention required                |
| Organized storage in S3    | Easy to retrieve old image versions            |
| Space-efficient            | Temporary local usage with cleanup             |
| Disaster recovery          | Restore ECR images from S3 backups             |
| Cross-region portability   | Backed-up images can be pushed to other regions|

---

## **Challenges & Considerations**

- **Storage Costs**: Frequent backups might increase S3 storage usage
- **Image Size**: Docker images can be large; monitor disk usage
- **Rate Limits**: AWS CLI and Docker Hub may throttle heavy usage
- **Security**: Ensure IAM permissions are restricted to necessary operations

---

## **Improvements & Enhancements**

| Enhancement                    | Description                                        |
|-------------------------------|----------------------------------------------------|
| Cron Job Integration          | Schedule backups periodically (e.g., daily)        |
| Logging & Monitoring          | Integrate with CloudWatch for auditing             |
| Compression                   | Save images as `.tar.gz` to reduce size            |
| Parallelization               | Use `xargs -P` or background processes for speed   |
| Restoration Script            | Create a complementary script to restore backups   |

---

## **Conclusion**

This solution provides a lightweight and effective mechanism to safeguard ECR container images in Amazon S3. By combining AWS CLI, Docker, and Bash scripting, teams can implement robust backup strategies without relying on third-party tools or managed services.
