# Test Case Backend

## Arquitectura hexagonal

La arquitectura hexagonal tiene las siguientes capas:
- **infraestructure/adapters**: es la capa que se conecta con el exterior como la base de datos o los controladores.
- **application/ports**: es la capa intermediaria que se conecta con la infraestructura.
- **domain**: es el núcleo, el core o la capa del negocio.

## Estructura y/o paquetes

Este proyecto tiene la siguiente estructura:
- **client**: es la capa cliente por ejemplo para usar las tablas, payments o shopping externo (en este caso no es necesario).
- **authentication**: conecta todos los microservicios rest mediante un token, así es más seguro la comunicación (no es el caso, pero sería recomendable, se refuerza la seguridad).
- **database**: se usan los repositorios jpa para realizar consultas a la base de datos en memoria h2 o mongo db. En este caso está dentro del paquete adapters y después en el paquete database.
- **domain**: es la capa que se encarga de solucionar la lógica de negocio. Aquí se encuentran los servicios que tienen la lógica del negocio.
- **entity/model**: son todas las entidades de la lógica del dominio, y en este caso está dentro de la carpeta de dominio.
- **web**: es el starter, la api web y tiene la configuración de la base de datos de mongo db.

En este proyecto se ha dispersado las capas que tiene teóricamente la arquitectura hexagonal, así la lógica del negocio está desacoplada con el exterior. Es decir, si se realiza algún cambio en el exterior no tiene que afectar en la lógica del negocio, y viceversa.

En la **capa de infraestructura/adapters** estarían los paquetes `database`, `api`, `authentication` y `client`.

En la **capa de ports** estarían `services`.

En la **capa del dominio** entran los paquetes `domain` y `model`.

La mejor forma sería realizar de forma modulada, ya que cada uno tendría su configuración de dependencias y cada módulo se relacionaría con la capa correspondiente.

Se ha usado mockito para realizar los tests unitarios desde un fichero json.

Los ficheros json están en la carpeta `test`, en la carpeta `java`, en la carpeta `resources` y después en el paquete `json`.

## Paquetes
 ### Model o entity

 Son las entidades de la lógica del dominio como `SensorEventModel`. 

 La entidad `SensorEventModel` contiene las anotaciones `@Document`, `@Data`, `@Id` y `@Transient` para la persistencia de datos en MongoDB. La anotación `@Document` sirve para que la aplicación sepa que es una entidad y que debe crear la tabla según las propiedades que tenga la clase. La anotación `@Id` sirve para definir el identificador de la clase. La anotación `@Data` es para saber que lleva los datos y `@NotBlank` o `@NotNull` sirven para las validaciones, sobre cuando se tiene que crear o actualizar los datos.

  ```java
  @Document(collection = "sensor_events")
  @Data
  public class SensorEventModel {
  
    @Id
    @NotBlank
    @JsonProperty("sensorId")
    private String sensorId;

    @NotNull
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
  
  }
  ```

  Aparte de las anotaciones de MongoDB la clase `SensorEventModel` tiene las anotaciones de Lombok reducir el código repetitivo como `@NoArgsConstructor` y `@JsonInclude(JsonInclude.Include.NON_NULL)`.

  ### Database

  Se ha usado el repositorio de MongoDB para realizar consultas o actualizaciones a la base de datos en MongoDB. Se suele crear los repositorios necesarios por cada entidad. En el caso de la interfaz `SensorEventRepository` se extiende el repositorio de MongoDB para utilizar la entidad `SensorEventModel`:
  
  ```java
    @Repository
    public interface SensorEventRepository extends MongoRepository<SensorEventModel, String>{

    }
  ```
  La configuración de la base de datos de MongoDB y del puerto están dentro de la carpeta `resources` en el fichero de `application.properties`:

  ```properties
    spring.application.name=sensor_event
    server.port=8090
    # MONGODB (MongoProperties)
    spring.data.mongodb.uri=mongodb://localhost:27017/sensor_events
  ``` 

  ### Ports

  En este paquete están las interfaces de los servicios que son intermediarios entre los adaptadores y el dominio.

  La interfaz `SensorEventService` tiene los métodos CRUD para que se puedan implementar en la lógica del negocio.

  ```java
    public interface SensorEventService {
    
        SensorEventModel create(SensorEventRqDto sensorEventRqDto);
    
        List<SensorEventModel> getAllSensorEvents();
    
        SensorEventModel getById(String sensorId) throws ResourceNotFoundException;
    
        SensorEventModel update(String sensorId, SensorEventRqDto sensorEventRqDto) throws ResourceNotFoundException;
    
        boolean delete(String sensorId) throws ResourceNotFoundException;
    
    }
  ```

  ### Domain

  Se encarga de solucionar la lógica de negocio con los servicios necesarios por cada entidad. El paquete `domain` contiene el paquete `model` y después están las implementaciones de los servicios que se encargan de obtener y de devolver los datos a los controladores.

  En este paquete los `services` se suele crear por cada entidad. La clase `SensorEventServiceImpl` tiene una anotación `@Service` y `@Slf4j`. La primera es para que Spring Boot sepa que es un servicio y la segunda sirve para ver los registros de los logs.

  Esta clase tiene una inyección de dependencias en el constructor de la clase `SensorEventServiceImpl`:
  
  ```java

    @Slf4j
    @Service
    public class SensorEventServiceImpl implements SensorEventService {

        private final SequenceGeneratorService sequenceGeneratorService;
        private final SensorEventsMapperImpl sensorEventMapper;
        private final SensorEventRepository sensorEventRepository;

        public SensorEventServiceImpl(SequenceGeneratorService sequenceGeneratorService,
                                      SensorEventsMapperImpl sensorEventMapper,
                                      SensorEventRepository sensorEventRepository){
            this.sequenceGeneratorService = sequenceGeneratorService;
            this.sensorEventMapper = sensorEventMapper;
            this.sensorEventRepository = sensorEventRepository;
        }
    }
  ```
  Además, implemente la interfaz `SensorEventService` para que pueda crear, leer o actualizar los datos en la base de datos:

  ```java

    @Slf4j
    @Service
    public class SensorEventServiceImpl implements SensorEventService {

        public List<SensorEventModel> getAllSensorEvents(){
            return sensorEventRepository.findAll();
        }

        public SensorEventModel getById(String sensorId)
                throws ResourceNotFoundException {

            return sensorEventRepository.findById(sensorId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.ID_NOT_FOUND + sensorId));
        }
    }
  ```
  ### Mapper
  
  Los mappers sirven para realizar las conversiones entre una clase a otra y así se puede encapsular la entidad o el modelo que lleva los datos a guardar en la base de datos.

  Por ejemplo, el controlador tiene una petición de un dto y para guardar se utiliza la clase modelo. En este caso se necesita la conversión tanto para la petición como para la respuesta, así que se han creado varios métodos para la petición específica del dto, para un dto genérico, para el modelo y para las listas.

  A continuación se muestra un ejemplo sobre un mapper utilizado `mapstruct`:

   ```java
    @Mapper(componentModel = "spring")
    public abstract class SensorEventMapper {
    
        public abstract SensorEventDto mapRqDtoToDto(SensorEventRqDto request);
    
        public abstract SensorEventModel mapDtoToModel(SensorEventDto request);
    
        public abstract SensorEventDto mapModelToRto(SensorEventModel model);
    
        public List<SensorEventDto> mapListModelsToListDto(List<SensorEventModel> modelList) {
            List<SensorEventDto> list = new ArrayList<>(modelList != null ? modelList.size() : 0);
            for (Object m : (modelList != null) ? modelList : List.of()) {
                list.add(mapModelToRto((SensorEventModel) m));
            }
            return list;
        }
    }
  ```
  ### DTO

  En este caso se ha utilizado dos tipos de dto, uno simplemente para la request o petición (específica) y otra dto de forma genérica, es decir, simplemente para realizar la conversión entre el modelo y el dto, y viceversa.

  Ejemplo de dos tipos de dto:

  - Request DTO (para crear o actualizar los datos):

     ```java
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class SensorEventRqDto {
    
        @NotNull
        @JsonProperty("type")
        private SensorTypeEnum type;
    
        @NotNull
        @JsonProperty("value")
        private Double value;
    }
    ```

  - DTO genérica:

     ```java
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class SensorEventDto {
    
        @Id
        @JsonProperty("sensorId")
        private String sensorId;
    
        @JsonProperty("timestamp")
        private OffsetDateTime timestamp;
    
        @JsonProperty("type")
        private SensorTypeEnum type;
    
        @JsonProperty("value")
        private Double value;
    }
    ```
  ### Controller

  El controlador es que contiene la API de la web dónde se puede ver el swagger de la aplicación con la [url](http://localhost:8090/swagger-ui/index.html#/).

  El controlador lleva la anotación de `@RestController` para que se sepa que es un controlador REST y la anotación `@RequestMapping("/api/sensor-events")` para la base del mapeo de los endpoints.

  El controlador `SensorEventController` lleva inyectado tanto el servicio como el mapper para que se puedan consultar o actualizar los datos como realizar las conversiones necesarias para dar la respuesta adecuada.

  Ejemplo del controlador:

   ```java
    @RestController
    @RequestMapping("/api/sensor-events")
    public class SensorEventController {
    
        private final SensorEventService sensorEventService;
        private final SensorEventsMapperImpl sensorEventMapper;
    
        public SensorEventController(SensorEventService sensorEventService,
                                     SensorEventsMapperImpl sensorEventMapper){
            this.sensorEventService = sensorEventService;
            this.sensorEventMapper = sensorEventMapper;
        }
    }
   ```
 
   Ejemplo de dos endpoints:

   ```java
    @RestController
    @RequestMapping("/api/sensor-events")
    public class SensorEventController {

        @GetMapping("/list-all")
        public List<SensorEventDto> getAllSensorEvents() {
            List<SensorEventModel> listSensorModel = sensorEventService.getAllSensorEvents();
            return sensorEventMapper.mapListModelsToListDto(listSensorModel);
        }
    
        @GetMapping(Constants.ENDPOINT_MAPPING_ID)
        public ResponseEntity<SensorEventDto> getSensorEventById(
                                                @PathVariable(value = Constants.PATH_VARIABLE_ID) String sensorId)
                                                throws ResourceNotFoundException {
    
            SensorEventModel model = sensorEventService.getById(sensorId);
            SensorEventDto rsDto = getRsDto(model);
    
            return ResponseEntity.ok().body(rsDto);
        }

        private SensorEventDto getRsDto(SensorEventModel model){
            return sensorEventMapper.mapModelToRto(model);
        }
        
    }
   ```
   Según se figura en el código del controlador el endpoint puede lanzar una excepción o puede devolver los datos.

## Tests con Mockito

Se han creado los tests para las clases `SensorEventServiceImpl` y `SensorEventController`. Se ha usado para realizar los tests `junit.jupiter` y `mockito`.

### Test de integración - Controller

Se ha realizado el test de integración del controller y para ello antes hay que ejecutar Kafka.

Hay que ejecutar el comando `.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties` (inicializado Zookeeper) y `.\bin\windows\kafka-server-start.bat .\config\server.properties` (inicializado Kafka) tanto en CMD, en PowerShell o en otro terminal.

Una vez arrancado Kafka, ya se pueden ejecutar los tests de integración.

## Dockerfile y docker-compose

Es un archivo de texto simple con un conjunto de comandos o instrucciones para realizar acciones en la imagen base a crear, ejemplo del `Dockerfile`:

```Dockerfile
FROM mediasol/openjdk17-slim-jprofiler

ARG JAR_FILE=target/test-case-backend-0.0.1-SNAPSHOT.jar

# Copy the Spring Boot JAR file into the container
COPY ${JAR_FILE} app.jar

EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]
```

El fichero `docker-compose.yml` se utiliza un contenedor con un maven para construir el paquete jar, y otro con open-jdk para su despliegue:

```yml
version: "3.8"
services:
  mongo_db:
    image: mongo:5.0.2
    restart: unless-stopped
    env_file: ./.env
    ports:
      - 27017:27017
  app:
    depends_on:
      - mongo_db
    build: ./test-case-backend-app
    restart: on-failure
    env_file: ./.env
    ports:
      - 8090:8090
    environment:
      SPRING_APPLICATION_JSON: '{
              "spring.data.mongodb.uri" : "mongodb://localhost:$MONGODB_DOCKER_PORT/$MONGODB_DATABASE"
            }'
      stdin_open: true
      tty: true
```

## Kafka

Apache Kafka crea eventos de un registro para saber las acciones que han sucedido y a la hora que han pasado. Simplemente, un cliente realiza una petición, realiza una acción en la aplicación y se queda registrado ese evento.

Kafka permite que se publiquen las secuencias de datos o eventos que se suscriban, almacenan los registros y procesa los datos en tiempo real (lo produce).

Primero hay que instalar Apache Kafka para que funcionen las colas de mensajes. Para ello hay que ir a la página oficial de [Apache Kafka](https://kafka.apache.org/quickstart) y descargar el fichero con la extensión `.tgz`. Una vez descargado, se extrae, la carpeta de Kafka se renombra y se mueve a la raíz del disco duro.

Una vez movido la carpeta llamada `Kafka`, hay que modificar los ficheros de configuración de la ruta `C:\Kafka\config`. Hay que modificar en el caso de Windows los ficheros `server.properties` y `zookeeper.properties`.

En el fichero `server.properties` hay que editar la ruta de los logs, en este caso se ha puesto la ruta `log.dirs=C:/Kafka/kafka-logs`.

En el fichero `zookeeper.properties` hay que editar la ruta de data dir, en este caso se ha puesto la ruta `dataDir=C:/Kafka/zookeeper-data`.

Una vez modificado esos ficheros hay que ejecutar como Administrador el terminal cmd o Powershell los siguientes comandos:

```txt

- Inicia Zookeeper:
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

- Inicia Kafka:
.\bin\windows\kafka-server-start.bat .\config\server.properties

- Crea un nuevo topic en el servidor de kafka:
.\bin\windows\kafka-topics.bat --create --topic sensor_events --bootstrap-server localhost:9092

- Decribe los detalles de un topic:
.\bin\windows\kafka-topics.bat --describe --topic sensor_events --bootstrap-server localhost:9092

- Listar todos los topics que existen dentro del broker:
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

- Inicia una consola para ver mensajes de un topic específico:
.\bin\windows\kafka-console-consumer.bat --topic sensor_events --bootstrap-server localhost:9092

- Inicia una consola para enviar mensajes a un topic específico:
.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic sensor_events
```

Añadir la dependencia en la aplicación del `pom.xml`:

```pom
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>3.0.4</version>
</dependency>
```


La configuración de Kafka en `application.properties`:

```properties
# Kafka Broker Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=processed_sensor_events
```

Para producir los registros se tiene una configuración y el componente:

```java
@Configuration
@Data
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

```java
@Component
public class MessageProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String sensorEventDto) {
        kafkaTemplate.send(topic, sensorEventDto);
    }

}
```

Para obtener o consumir los registros se tiene una configuración y el componente:

```java
@Configuration
@EnableKafka
@Data
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, Constants.KAFKA_TOPIC);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
```

```java
@Component
public class MessageConsumer {

    @KafkaListener(topics = Constants.KAFKA_TOPIC, groupId = Constants.KAFKA_GROUP_ID)
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }

}
```

Y para que se pueda guardar se ha implementado una llamada al método `sendMessage` en el controlador `SensorEventController`:

```java
@RestController
@RequestMapping("/api/sensor-events")
public class SensorEventController {

    @PostMapping
    public ResponseEntity<SensorEventDto> createSensorEvent(@Valid @RequestBody SensorEventRqDto sensorEventRqDto) {
        SensorEventModel model = sensorEventService.create(sensorEventRqDto);
        SensorEventDto rsDto = getRsDto(model);
        sendMessageKafka(rsDto);
        return ResponseEntity.ok(rsDto);
    }

    private void sendMessageKafka(SensorEventDto rsDto){
        if(!Objects.isNull(rsDto)){
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getSensorId());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getTimestamp().toString());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getType().toString());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getValue().toString());
        }
    }
}
```

Una vez que se ejecute la aplicación de Java con Spring Boot, tenemos que tener la terminal en marcha con el comando `.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties` (inicializado Zookeeper) y `.\bin\windows\kafka-server-start.bat .\config\server.properties` (inicializado Kafka) para ver los mensajes que se producen en el terminal desde cualquier IDE. 

Si se quiere visualizar los mensajes desde el CMD o PowerShell simplemente hay que ejecutar el comando `.\bin\windows\kafka-console-consumer.bat --topic sensor_events --bootstrap-server localhost:9092`.

## Lista de dependencias

| Dependencia                    |                                          Descripción                                           |
|--------------------------------|:----------------------------------------------------------------------------------------------:|
| Spring Boot Starter Web        |                                   Arranque de la aplicación                                    |
| Spring Boot Starter Validation |                    Es un starter para realizar más fácil las validaciones.                     |
| Spring Boot DevTools           |                          Herramienta de desarrollo como ver los logs.                          |
| Spring Boot Starter Test       |                                Se realizan los test unitarios.                                 |
| MongoDB                        |                                  Base de datos no relacional.                                  |
| Lombok                         |            Biblioteca de anotaciones Java que ayuda a reducir el código repetitivo.            |
| Mapstruct                      |                       Para crear y manejar los mapeos de forma sencilla.                       |
| Springdoc OpenApi              | Sirve para documentar los endpoints, y así se puede probar y exportar para el equipo de front. |
| Jackson Datatype Joda          |        Instancia de ObjectMapper para serializar respuestas y deserializar solicitudes.        |
| Common io                      |         Convierte JSON a objetos y admite una fácil conversión a Map desde datos JSON.         |
| Spring Kafka                   |                               Se usa para las colas de mensajes.                               |