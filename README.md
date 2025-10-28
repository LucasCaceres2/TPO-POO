# Plataforma de Cursos

## Estructura del proyecto

- **modelo**: Contiene todas las clases definidas en el diagrama de clases.
- **dto**: Los DTOs permiten traer solo los atributos que necesitamos de un objeto, evitando traer todo el objeto completo.
    - **¿Para qué sirve el DTO?**  
      Sin el DTO, se pueden generar **loops infinitos** porque los objetos referencian otros objetos que a su vez los referencian, lo que provoca errores al serializar a JSON o al manejar datos.
- **app**: Contiene el `Main` para probar la lógica de la plataforma.
- **persistencia (GestorDePersistencia)**: Maneja el guardado y la lectura de datos en archivos JSON.
    - Actualmente existen dos archivos JSON: `usuarios.json` y `cursos.json`.
    - Futuras extensiones podrían incluir `areas.json` y `pagos.json`.

## Clases pendientes de lógica o revisión

- `Area`
- `Inscripcion`
- `Pago`
- `Plataforma`

## Próximos pasos

Después de implementar y probar toda la lógica de estas clases, se incorporará la **interfaz gráfica**.
