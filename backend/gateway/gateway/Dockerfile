# Build stage 
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Rodando o maven para gerar o jar da aplicação, sem rodar os testes, ignora (com DskipTests) para não precisar baixar dependencias de testes e rodar os testes, apenas o necessario para gerar o jar da aplicação
RUN mvn -q clean package -DskipTests

# Apenas o necessario para rodar a aplicação
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiando o jar gerado na etapa de build para a imagem final
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]