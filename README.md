O **Order Management Service** é uma aplicação Spring Boot para gerenciar pedidos e itens associados. A aplicação suporta operações CRUD através de endpoints REST e integrações com RabbitMQ para processamento de mensagens.

---

## **Pré-requisitos**

Antes de iniciar, certifique-se de que possui os seguintes softwares instalados:

- **Java**: Versão 17 ou superior
- **Maven**: Versão 3.8.1 ou superior
- **Docker**: Para rodar o RabbitMQ
- **Postman** (opcional): Para testar os endpoints REST
- **RabbitMQ Management Console** (opcional): Para monitorar filas de mensagens

---

## **Instalação e Configuração**

### **Passo 1: Clonar o Repositório**
Clone este repositório para o seu ambiente local:
```bash
git clone <repository-url>
cd order-management-service
```

---

### **Passo 2: Compilar o Projeto**
```bash
mvn clean install
```
---
### **Passo 3: Configurar o RabbitMQ**

Inicie o RabbitMQ usando Docker:

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

Verifique se o RabbitMQ está ativo acessando o console:

URL: http://localhost:15672
Usuário: guest
Senha: guest

### **Passo 4: Executar a Aplicação**

Inicie a aplicação com o comando:

```bash
mvn spring-boot:run
```
A aplicação será iniciada em http://localhost:8080.

### **Passo 5: Configuração do Banco de Dados**

A aplicação utiliza o banco de dados em memória H2. Para acessar o console do H2:

URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Usuário: sa
Senha: (em branco)
🌐 Endpoints REST

### **1. Criar um Pedido**
```bash
Método: POST
URL: /api/orders
Corpo do JSON:
json

{
  "externalOrderId": "order-12345",
  "items": [
    {
      "productName": "Notebook",
      "price": 3000.00,
      "quantity": 1
    },
    {
      "productName": "Mouse",
      "price": 150.00,
      "quantity": 2
    }
  ]
}
Resposta:
json

{
  "id": 1,
  "externalOrderId": "order-12345",
  "createdAt": "2024-12-10T09:00:00",
  "status": "PENDING",
  "totalPrice": 3300.00,
  "items": [
    {
      "id": 1,
      "productName": "Notebook",
      "price": 3000.00,
      "quantity": 1
    },
    {
      "id": 2,
      "productName": "Mouse",
      "price": 150.00,
      "quantity": 2
    }
  ]
}
```
---
### **2. Buscar Pedido por ID**
```bash
Método: GET
URL: /api/orders/{id}
GET /api/orders/1
3. Listar Todos os Pedidos
Método: GET
URL: /api/orders
```
---
### **4. Atualizar o Status de um Pedido**
```bash
Método: PUT
URL: /api/orders/{id}/status?status={status}
PUT /api/orders/1/status?status=PROCESSED
```
---
### **5. Deletar um Pedido**
```bash
Método: DELETE
URL: /api/orders/{id}
DELETE /api/orders/1
```
---
### **Integração com RabbitMQ**

Filas Criadas
incoming-orders: Recebe novos pedidos para processamento.
processed-orders: Envia pedidos processados para outros sistemas.
Publicar Mensagem na Fila incoming-orders
Use o RabbitMQ Management Console ou um script para publicar mensagens na fila.

```bash
Mensagem de Exemplo:

json

{
  "externalOrderId": "order-12345",
  "items": [
    {
      "productName": "Notebook",
      "price": 3000.00,
      "quantity": 1
    },
    {
      "productName": "Mouse",
      "price": 150.00,
      "quantity": 2
    }
  ]
}
```

Consumir Mensagem da Fila processed-orders

Após o processamento, os pedidos serão publicados nesta fila. Use o console ou uma aplicação para monitorar as mensagens.

🛠️ Testes
Testar com Postman
Importe a coleção de endpoints do projeto (se disponível) ou configure manualmente os endpoints descritos acima.
Teste cada operação para verificar o funcionamento.
Monitorar Mensagens
Use o console do RabbitMQ para verificar as mensagens nas filas incoming-orders e processed-orders.