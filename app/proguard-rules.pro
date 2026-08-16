# Reglas mínimas. La app no ofusca en debug; en release se mantienen
# modelos de Room/dominio para evitar problemas de reflexión.
-keep class com.educalab.staticdata.data.local.entity.** { *; }
-keep class com.educalab.staticdata.domain.model.** { *; }
