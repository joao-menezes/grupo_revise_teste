# Simulador de Frete

API REST para simular fretes entre dois CEPs e retornar tanto a opção mais barata quanto a mais rápida entre as transportadoras cadastradas.

O projeto foi desenvolvido como desafio técnico. Toda a configuração das transportadoras fica armazenada no banco de dados, incluindo preço, limite de peso, seguro, estados bloqueados, tipos de carga aceitos e prazo de entrega. Dessa forma, o motor de cálculo apenas aplica as regras cadastradas, sem depender de lógica específica para cada transportadora.

## Stack

- Java 21
- Spring Boot 4.1.0
- PostgreSQL
- Flyway
- Redis
- Docker

## Executando o projeto

```bash
docker compose up -d
./mvnw spring-boot:run
```

A aplicação ficará disponível na porta `8080`.

Ao iniciar, o Flyway cria toda a estrutura do banco e insere automaticamente as sete transportadoras utilizadas pelo sistema.

## Testando a API

Faça uma requisição:

**POST**

`http://localhost:8080/api/frete/simular`

### Headers

```
Content-Type: application/json
```

### Body

```json
{
  "cepOrigem": "20040020",
  "cepDestino": "01310930",
  "valorPedido": 500.00,
  "peso": 5.5,
  "tipoCarga": "GERAL"
}
```

Os tipos de carga aceitos são:

- GERAL
- FRAGIL
- QUIMICA

### Exemplo de resposta

```json
{
  "origem": {
    "cidade": "Rio de Janeiro",
    "estado": "RJ"
  },
  "destino": {
    "cidade": "São Paulo",
    "estado": "SP"
  },
  "freteMaisBarato": {
    "transportadora": "EcoTrans",
    "valor": 45.70,
    "prazoDias": 3
  },
  "freteMaisRapido": {
    "transportadora": "RápidaLog",
    "valor": 240.00,
    "prazoDias": 2
  }
}
```

## Possíveis respostas de erro

| Situação | Status |
|----------|--------|
| Payload inválido (peso negativo, campos obrigatórios ausentes ou CEP inválido) | `400` |
| CEP não encontrado | `404` |
| Nenhuma transportadora atende aos requisitos do pedido | `422` |

## Como a distância é calculada

A ideia inicial era utilizar as coordenadas retornadas pela BrasilAPI para calcular a distância entre os CEPs. Na prática isso não funcionou, porque boa parte dos CEPs é resolvida pelo OpenCEP, que normalmente não retorna informações de latitude e longitude.

A solução adotada foi consultar a BrasilAPI para obter cidade e estado de cada CEP. Depois disso, essas informações são enviadas ao Nominatim (OpenStreetMap), que retorna as coordenadas da cidade. Com esses pontos é aplicada a fórmula de Haversine para estimar a distância.

Esse cálculo representa uma distância em linha reta, portanto não corresponde ao trajeto real de uma rodovia. Para obter distâncias reais seria necessário utilizar uma API de rotas, como o OSRM, o que ficou fora do escopo do desafio.

As consultas feitas à BrasilAPI e ao Nominatim ficam armazenadas em cache no Redis por 24 horas, reduzindo chamadas repetidas para os mesmos locais.

## Executando os testes

```bash
./mvnw test
```

Os testes do `FreteCalculatorService` verificam principalmente casos de fronteira, como limites de peso e de valor do pedido, garantindo que as regras sejam aplicadas corretamente. Eles utilizam mocks e não dependem de PostgreSQL nem de Redis em execução.

## Algumas decisões de implementação

- O Hibernate foi configurado com `ddl-auto: validate`, deixando a criação e versionamento do banco sob responsabilidade do Flyway.
- A validação de estados bloqueados considera apenas o estado de destino.
- Não foram mantidos endpoints de apoio ou debug na versão final. As validações feitas durante o desenvolvimento foram transformadas em testes automatizados.
