# Guía: App Android conectada a una aplicación web (HTTP + JSON)
**Estudiante:** Haider Pabón Mejía

Basado en la guía "Desarrollo de App Android conectadas a una aplicación web", de John Carlos Arrieta Arrieta.

## Descargar la app (APK)
APK firmado (release): [`apk/ConexionHttp-HaiderPabon-v1.0.apk`](apk/ConexionHttp-HaiderPabon-v1.0.apk)

La app se conecta a `http://10.0.2.2/crudphpjson/crud/operacion.php` (el PC visto desde el emulador de Android Studio).
Usuario de prueba: `haider@gmail.com` / `1234`.

## Qué hay en esta carpeta

```
guia_android_http_haider_pabon_mejia/
├── base_datos/crudphpjson.sql          → crea la BD crudphpjson y la tabla Usuarios
├── servidor_php/crudphpjson/           → copiar a C:\wamp64\www\
│   ├── index.php
│   ├── bd/conexion_bd.php
│   └── crud/operacion.php
└── android/EjemploConexionHttp/        → abrir con Android Studio
    └── app/src/main/java/haiderpabon/guias/ejemploconexionhttp/
        ├── datos/         Usuario.java, Mensaje.java
        ├── controladores/ ConexionHttpPostServer.java
        └── vistas/        PantallaInicio, PantallaCrudUsuario, PantallaListado
```

## Paso 1: Base de datos
1. Abre WAMP y espera a que el ícono se ponga **verde**.
2. Abre phpMyAdmin (http://localhost/phpmyadmin) o NetBeans → Prestaciones → MySQL → Ejecutar comando.
3. Ejecuta el archivo `base_datos/crudphpjson.sql`.
   Esto crea la BD, la tabla `Usuarios` y un usuario de prueba:
   **email:** `haider@gmail.com` · **clave:** `1234`

## Paso 2: Servicio HTTP en PHP
1. Copia la carpeta `servidor_php/crudphpjson` dentro de `C:\wamp64\www\`
   (debe quedar `C:\wamp64\www\crudphpjson\crud\operacion.php`).
2. Prueba en el navegador:

| Prueba | URL | Respuesta esperada |
|---|---|---|
| Servicio activo | `http://localhost/crudphpjson/` | `{"mensaje":"Servicio HTTP crudphpjson funcionando",...}` |
| Agregar | `http://localhost/crudphpjson/crud/operacion.php?accion=Agregar&email=ana@gmail.com&psw=abc&nombre=ANA` | `{"mensaje":"OK"}` |
| Login correcto | `...operacion.php?accion=login&email=haider@gmail.com&psw=1234` | los datos del usuario |
| Login incorrecto | `...operacion.php?accion=login&email=x@x.com&psw=1` | `{"mensaje":"Acceso denegado"}` |
| Editar | `...operacion.php?accion=editar&email=ana@gmail.com&psw=999&nombre=ANA MARIA` | `{"mensaje":"OK"}` |
| Listar | `...operacion.php?accion=listar` | `[{...},{...}]` |
| Eliminar | `...operacion.php?accion=eliminar&email=ana@gmail.com` | `{"mensaje":"OK"}` |

## Paso 3: App Android
1. Android Studio → **File → Open** → selecciona la carpeta `android/EjemploConexionHttp`.
2. Espera a que termine **Gradle Sync** (necesita internet la primera vez: descarga
   HttpClient, GSON y AppCompat automáticamente, ya no hace falta copiar los .jar a `libs`).
3. Revisa la URL del servidor en `controladores/ConexionHttpPostServer.java`:
   - **Emulador:** `http://10.0.2.2/crudphpjson/crud/operacion.php` (ya viene así)
   - **Celular físico** (misma red WiFi que el PC): cambia `10.0.2.2` por la IP de tu PC
     (en CMD escribe `ipconfig` → "Dirección IPv4", por ejemplo `192.168.1.31`).
     En WAMP puede que tengas que permitir el acceso desde la red (Apache "Require all granted")
     y dar permiso a Apache en el firewall de Windows.
4. Dale **Run ▶**.

### Cómo funciona la app
- **Pantalla de inicio:** email + clave → botón *Entrar* (login) o *Regístrate* (nuevo usuario).
- **Listado:** después de entrar, muestra todos los usuarios de la BD.
- **Edita tus datos:** abre el formulario con tus datos para modificarlos (el email no se cambia porque es la llave primaria).

## Correcciones hechas al código del PDF
El código de la guía tenía algunos errores que impedían que funcionara en Android actual:

1. **`usesCleartextTraffic="true"`** en el Manifest: desde Android 9, sin esto se bloquean las conexiones `http://`.
2. **Toast dentro de `doInBackground`** (hilo secundario) hacía que la app se cerrara si fallaba la conexión. Ahora el error se muestra en `onPostExecute`.
3. `operacion.php` respondía `"OK)"` (con paréntesis) al guardar, por eso la app siempre decía "Respuesta Incorrecta". Corregido a `"OK"`.
4. El listado se caía cuando no había usuarios (`{"mensaje":...}` no es una lista). Ahora se detecta.
5. Las actividades de la guía estaban en paquetes distintos al del Manifest; ahora todo está en `vistas` y el Manifest apunta ahí.
6. La lectura de la respuesta usa **UTF-8** (antes iso-8859-1, dañaba las tildes).
7. Se quitó `Thread.sleep(2000)` del hilo principal (congelaba la pantalla).
8. En PHP se escapan los datos recibidos (`real_escape_string`) para que nombres con comillas, como O'Neil, no rompan el SQL.
9. La URL del servidor quedó en un solo lugar (`ConexionHttpPostServer.URL_SERVICIO`).

## Nota: ejecución con XAMPP (en vez de WAMP)
- Copiar `servidor_php/crudphpjson` a `C:\xampp\htdocs\crudphpjson`.
- Para crear la base de datos basta con abrir **http://localhost/crudphpjson/instalar.php** una vez (ejecuta `crudphpjson.sql`).
- Si hay otro MySQL instalado (servicio **MySQL80**) debe estar detenido, porque usa el mismo puerto 3306.
- Orden para ejecutar: MySQL80 detenido → XAMPP con Apache y MySQL en verde → Run ▶ en Android Studio.
