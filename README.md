# Simulador de Frete

API REST que simula frete entre dois CEPs e devolve a opção mais barata e a mais rápida entre as transportadoras cadastradas. Foi feita como desafio técnico, então tem algumas decisões de escopo comentadas mais abaixo.

A ideia central: nenhuma regra de transportadora fica no código. Tudo — preço, peso aceito, seguro, UF bloqueada, tipo de carga, prazo — mora no banco. O motor de cálculo só sabe aplicar fórmulas genéricas em cima do que está lá.

## Stack

Java 21, Spring Boot 4.1.0, PostgreSQL + Flyway, Redis, Docker.

## Subindo o projeto

```bash
docker compose up -d
./mvnw spring-boot:run
```

Sobe na `8080`. O Flyway já cria as tabelas e popula as 7 transportadoras sozinho, não precisa rodar nada no banco na mão.

## Testando no Postman / Insomnia

Cria uma requisição nova com isso:

**Método:** `POST`
**URL:** `http://localhost:8080/api/frete/simular`

**Headers:**
Content-Type: application/json

**Body (raw / JSON):**
```json
{
  "cepOrigem": "20040020",
  "cepDestino": "01310930",
  "valorPedido": 500.00,
  "peso": 5.5,
  "tipoCarga": "GERAL"
}
```

`tipoCarga` aceita `GERAL`, `FRAGIL` ou `QUIMICA`. CEP pode ir com ou sem hífen, a API normaliza sozinha.

Resposta esperada:

```json
{
  "origem": { "cidade": "Rio de Janeiro", "estado": "RJ" },
  "destino": { "cidade": "São Paulo", "estado": "SP" },
  "freteMaisBarato": { "transportadora": "EcoTrans", "valor": 45.70, "prazoDias": 3 },
  "freteMaisRapido": { "transportadora": "RápidaLog", "valor": 240.00, "prazoDias": 2 }
}
```

Se quiser deixar salvo como environment variable no Postman/Insomnia, cria uma `base_url = http://localhost:8080` e usa `{{base_url}}/api/frete/simular` na URL — facilita se depois você for testar contra outro ambiente.

### Erros

| Situação | Status |
|---|---|
| Payload mal formado (peso negativo, campo faltando, CEP com letra) | `400` |
| CEP não existe | `404` |
| Nenhuma transportadora atende as regras do pedido | `422` |

## Sobre o cálculo de distância

Essa foi a parte mais chata do desafio. A ideia inicial era pegar a coordenada que a BrasilAPI devolve junto com o CEP e calcular a distância direto. Só que na prática a maioria dos CEPs cai num provedor (`open-cep`) que não preenche coordenada nenhuma — testei um punhado de CEPs de capitais diferentes e nenhum veio com `location` preenchido.

Solução: a BrasilAPI resolve CEP pra cidade/estado (isso nunca falha), e a distância vem de geocodificar cidade/estado no Nominatim (OpenStreetMap) e aplicar Haversine em cima. É distância em linha reta, não rota de estrada real — dá uma aproximação razoável, mas não é o valor exato que uma transportadora cobraria numa rota rodoviária de verdade. Pra isso precisaria de uma API de rotas tipo OSRM, que ficou fora do escopo.

Ambas as chamadas (BrasilAPI e Nominatim) ficam cacheadas no Redis por 24h, então na prática cada cidade só bate na internet uma vez.

## Rodando os testes

```bash
./mvnw test
```

Os testes do `FreteCalculatorService` cobrem os casos de fronteira que mais gostam de dar bug: peso exclusivo vs inclusivo, o limite exato onde uma NF-e passa a bloquear ou liberar uma transportadora, esse tipo de coisa. Rodam com mock, não dependem de Postgres ou Redis de pé.

## Umas decisões que vale registrar

- `ddl-auto: validate` — o Flyway é dono do schema, o Hibernate só confere se bate. Nunca deixo o JPA criar tabela sozinho.
- Bloqueio de UF é checado contra o **destino**, não a origem. Assumi que "não atende tal região" quer dizer não entrega lá.
- Não deixei nenhum endpoint de debug/listagem de todas as transportadoras na versão final — o que precisei validar manualmente durante o desenvolvimento virou teste automatizado em vez de ficar exposto como rota.
