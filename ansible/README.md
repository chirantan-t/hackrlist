# Ansible Configuration Management Guide

**Project:** Dockerized Hackathon Team Management System  
**Framework:** Ansible 2.x Automation & Environment Provisioning  

---

## 1. Overview
This directory contains modular, idempotent Ansible automation designed to configure target Linux hosts, verify operating system compatibility, establish restricted deployment users, deploy the Docker multi-container stack, and validate service health via Spring Boot Actuator probes.

## 2. Directory Layout
```text
ansible/
├── inventory.ini           # Inventory definitions (local, staging, production)
├── playbook.yml            # Main entry orchestration playbook
├── group_vars/
│   └── all.yml             # Global variables (ports, paths, image versions)
├── roles/
│   ├── common/             # System packages and deployment user creation
│   ├── docker/             # Docker engine and group membership
│   ├── app/                # App directory, .env template, and container launch
│   └── nginx/              # Nginx reverse proxy templates
└── README.md               # Operations and troubleshooting documentation
```

---

## 3. Prerequisites
* **Control Node (Linux / WSL2 / CI Agent):**
  - Python 3.10+
  - Ansible Core 2.15+ (`pip install ansible`)
  - `community.docker` collection (`ansible-galaxy collection install community.docker`)
* **Managed Nodes (Target Servers):**
  - Ubuntu 22.04 LTS / Debian 12 / RHEL 9
  - SSH server with public key authentication
  - Sudo privileges for the execution user

---

## 4. Execution Instructions

### 4.1 Playbook Syntax & Dry-Run Validation
```bash
# Check syntax of playbook and roles
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check

# Dry-run execution (check mode)
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --check
```

### 4.2 Standard Deployment Execution
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

### 4.3 Overriding Release Version or Target Host
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml \
  --extra-vars "image_tag=1.0.1 host_port=8081" \
  --limit staging
```

---

## 5. Demonstrating Idempotency (Task 14)

Idempotency guarantees that executing the playbook multiple times produces identical state without making unnecessary modifications to already-correct resources.

### Verification Steps:
1. **First Execution (Provisioning):**
   ```bash
   ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
   ```
   * *Observed Result:* Tasks report `changed=X` as directories, users, and templates are established for the first time.
2. **Second Execution (Idempotency Check):**
   ```bash
   ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
   ```
   * *Observed Result:* All prerequisite and configuration tasks report `ok` and `changed=0`. Only explicit container re-launches (if tag changes) would trigger state updates.

---

## 6. Simulated Deployment Failure & Rollback Demonstration

To demonstrate disaster recovery and zero unverified downtime:

1. **Simulate Defective Release:**
   Execute deployment with an invalid or defective image tag:
   ```bash
   ansible-playbook -i ansible/inventory.ini ansible/playbook.yml \
     --extra-vars "image_tag=defective-broken-tag"
   ```
2. **Health Probe Failure:**
   The post-task health check probe to `http://localhost:8081/actuator/health` times out and returns failure.
3. **Trigger Rollback:**
   Execute the automated rollback script:
   ```bash
   /opt/hackathon-team-management-system/scripts/rollback.sh
   ```
4. **Recovery Confirmation:**
   The rollback script re-launches the verified stable tag (`1.0.0`) recorded in `.previous_stable_version` and confirms `HTTP 200 OK` from the Actuator health probe.
