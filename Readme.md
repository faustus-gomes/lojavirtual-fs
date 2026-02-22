# Formação e Mentoria Full-Stack em Spring Boot API Rest e Angular 16+

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1%2B-green)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-16%2B-red)](https://angular.io)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue)](https://www.postgresql.org)

A Formação e Mentoria Full-Stack em Spring Boot API Rest e Angular 16+ já está disponível para você começar a estudar!

## 🚀 Sobre o Projeto

Vamos criar um sistema de loja virtual completo desde o levantamento de requisitos até:
- Sistema financeiro
- Sistema de pagamento
- Configuração de servidores
- Implantação do sistema em produção

## ⏳ Duração

- Tempo estimado: 12 meses (1 ano)
- Aulas ao vivo: Toda terça-feira às 20:00 (Horário de Brasília)
- Conteúdo disponível 24h na área de estudos

## 📚 Conteúdo Atual

- 360 vídeo aulas
- +300 horas de conteúdo passo a passo
- Acesso às gravações das mentorias

## 🛠 Tecnologias

### Backend (Spring Boot)
- Spring Boot Rest API
- Spring Boot MVC
- Spring Data JPA
- Spring JDBC Template
- Spring Mock Test
- Java Mail
- Async e Scheduled
- API RestFull
- Integração com sistemas de pagamento (PIX, Boleto, Cartão)

### Frontend (Angular)
- Angular 16, 17, 15+
- ChartJS
- Componentes reutilizáveis
- Single Page Applications

### Banco de Dados
- SQL e PostgreSQL
- Modelagem de dados
- Normalização

### Cloud & DevOps
- AWS Cloud (EC2, RDS, S3)
- Elastic Beanstalk
- Implantação contínua

## 📋 Fluxo de Desenvolvimento Completo

1. **Análise e Planejamento**
    - Levantamento de requisitos
    - UML e documentação
    - Diagramação e fluxogramas
    - Modelagem de dados

2. **Desenvolvimento**
    - Arquitetura moderna para web
    - Padrões de projeto em Java
    - Implementação passo a passo
    - Testes (TDD)

3. **Integrações**
    - Nota fiscal eletrônica
    - Gateway de pagamento
    - Sistemas de transporte

4. **Recursos Avançados**
    - Gráficos gerenciais (ChartJS)
    - Relatórios financeiros
    - Controle de estoque
    - Loja virtual completa

## 🔥 Diferenciais

- Maior treinamento do mundo em Java + Spring Boot REST API com Angular
- Projeto real desde a concepção até a implantação
- Mentoria com acompanhamento personalizado
- Foco em soluções profissionais do mercado

## 📅 Próximos Passos

1. Acesse a área de membros
2. Assista as aulas introdutórias
3. Configure seu ambiente de desenvolvimento
4. Participe das mentorias ao vivo

**Bons estudos!** 🚀

## Definindo Email de Envio (gmail):

1. Ativar Verificação em 2 Etapas (se não tiver)

Acesse: https://myaccount.google.com/security
Ative "Verificação em 2 etapas"
2. Criar Senha de Aplicativo

Acesse: https://myaccount.google.com/apppasswords
Selecione "E-mail" e o dispositivo
Clique em "Gerar"
Use a senha de 16 caracteres gerada (sem espaços)

## ===========================================================
SEGURANÇA NA PRODUÇÃO:
✅ Versão Segura para Produção
...
public class HostIgnoringClient implements Serializable {
private static final long serialVersionUID = 1L;
private String hostName;

    public HostIgnoringClient(String hostName) {
        this.hostName = hostName;
    }

    public Client hostIgnoringClient() throws Exception {
        // PRODUÇÃO: Usa as configurações padrão seguras da JVM
        ClientConfig config = new ClientConfig();
        
        // Registra apenas os providers necessários
        config.register(JacksonJsonProvider.class);
        config.register(MultiPartFeature.class);
        
        // Timeouts razoáveis para produção
        config.property(ClientProperties.CONNECT_TIMEOUT, 10000);  // 10s
        config.property(ClientProperties.READ_TIMEOUT, 30000);     // 30s
        
        // Cria o cliente com configurações PADRÃO da JVM
        // NÃO configura SSL customizado
        // NÃO configura hostnameVerifier customizado
        Client client = ClientBuilder.newBuilder()
                .withConfig(config)
                .build();

        return client;
    }
}
...

...
# ⚠️ Configurações de Segurança para Produção

## 🚨 ATENÇÃO: A classe HostIgnoringClient NÃO deve ser usada em produção!

### Riscos de Segurança da versão de desenvolvimento:
- ❌ **TrustManager permissivo**: Aceita QUALQUER certificado, permitindo ataques Man-in-the-Middle
- ❌ **HostnameVerifier inseguro**: Aceita QUALQUER hostname, ignorando validação de identidade
- ❌ **Configurações globais**: Afeta toda a JVM com configurações inseguras

### ✅ Configuração Obrigatória para Produção:

1. **Use certificados válidos** (não auto-assinados) em produção
2. **Configure o TrustStore adequadamente**:
   ```properties
   # application.properties
   javax.net.ssl.trustStore=/caminho/para/truststore.jks
   javax.net.ssl.trustStorePassword=sua-senha
...

3. Remova configurações customizadas de SSL/TLS
4. Use timeouts apropriados (10-30 segundos)
📖 Boas Práticas:

Mantenha os certificados atualizados
Monitore conexões SSL/TLS
Use um keystore/truststore específico por ambiente
Considere usar um proxy reverso com terminação SSL

...

## 🎯 **Resumo das Mudanças para Produção**

| Configuração | Desenvolvimento | Produção |
|-------------|-----------------|----------|
| **TrustManager** | Aceita todos os certificados | Usa truststore da JVM |
| **HostnameVerifier** | Sempre retorna true | Verificador padrão |
| **SSLContext** | Customizado com TLS | Padrão da JVM |
| **Configurações globais** | `setDefaultHostnameVerifier()` | Removido |

## 💡 **Dica Extra: Profiles do Spring**

Use profiles para alternar automaticamente:

```java
@Configuration
public class ClienteConfig {
    
    @Bean
    @Profile("dev")  // APENAS para desenvolvimento
    public Client devClient() throws Exception {
        // Versão insegura (como a que criamos)
        return hostIgnoringClient();
    }
    
    @Bean
    @Profile("prod") // Para produção
    public Client prodClient() {
        // Versão segura
        ClientConfig config = new ClientConfig();
        config.register(JacksonJsonProvider.class);
        config.register(MultiPartFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 10000);
        config.property(ClientProperties.READ_TIMEOUT, 30000);
        
        return ClientBuilder.newBuilder()
                .withConfig(config)
                .build();
    }
}
...