🛠️ # Pré-requisitos (Linux)

Para usuários de Linux que possuem o VS Code mas ainda não configuraram o ambiente Java:

    Instalar o JDK (Java Development Kit):
    Abra o terminal (Ctrl+Alt+T) e execute:
    Bash

    sudo apt update
    sudo apt install openjdk-17-jdk -y

    Verificar Instalação:
    Bash

    java -version

    Extensão recomendada: No VS Code, instale o Extension Pack for Java da Microsoft para facilitar a execução.

🚀 #Como Executar

O sistema depende da execução simultânea de ambos os arquivos. Siga os passos abaixo no terminal do VS Code:
1. Preparar o Receptor (Receiver)

O receptor deve ser iniciado primeiro para começar a monitorar o grupo multicast 224.0.0.2 na porta 8877.
Bash

javac MulticastReceiver.java
java MulticastReceiver

2. Preparar o Emissor (Sender)

Em um segundo terminal, inicie o emissor que enviará o IP e o timestamp a cada 5 segundos.
Bash

javac MulticastSender.java
java MulticastSender

📋 #Funcionamento do Algoritmo

O detector opera através de duas threads principais no MulticastReceiver:

    Thread de Escuta: Captura as mensagens multicast e contabiliza as entradas recebidas por cada endereço IP.

    Thread de Monitoramento: Adormece por um período determinado (ex: 30 segundos) e, ao acordar, compara o volume de dados recebidos com o esperado.

Lógica de Cálculo de Suspeita

Se o intervalo de envio do Sender (Δ) é de 5 segundos e o monitoramento ocorre a cada 30 segundos, o número esperado de mensagens é 6. O nível de suspeita é calculado como:
Suspeita(%)=(Mensagens EsperadasMensagens Esperadas−Mensagens Recebidas​)×100

📂 #Estrutura do Projeto

    MulticastSender.java: Implementa o envio de datagramas para o grupo multicast com identificação do host.

    MulticastReceiver.java: Implementa a lógica multi-thread para recepção de dados e diagnóstico de falhas de omissão.
