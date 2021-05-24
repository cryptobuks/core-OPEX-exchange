package co.nilin.mixchange.auth.gateway.config

import org.keycloak.Config
import org.keycloak.representations.idm.RealmRepresentation

import org.keycloak.util.JsonSerialization

import org.springframework.core.io.ClassPathResource

import org.keycloak.services.managers.RealmManager

import org.keycloak.services.managers.ApplianceBootstrap

import java.util.NoSuchElementException

import org.keycloak.services.util.JsonConfigProviderFactory

import org.keycloak.services.resources.KeycloakApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import java.lang.Exception


class EmbeddedKeycloakApplication() : KeycloakApplication() {
    private val LOG: Logger = LoggerFactory.getLogger(EmbeddedKeycloakApplication::class.java)

    companion object {
        var keycloakServerProperties: KeycloakServerProperties? = null
    }

    override fun loadConfig() {
        val factory: JsonConfigProviderFactory = RegularJsonConfigProviderFactory()
        Config.init(factory.create()
            .orElseThrow { NoSuchElementException("No value present") })
    }

    init {
        createMasterRealmAdminUser()
        createMixchangeRealm()
    }

    private fun createMasterRealmAdminUser() {
        val session = getSessionFactory().create()
        val applianceBootstrap = ApplianceBootstrap(session)
        val admin = keycloakServerProperties!!.adminUser
        try {
            session.transactionManager.begin()
            applianceBootstrap.createMasterRealmUser(admin.username, admin.password)
            session.transactionManager.commit()
        } catch (ex: Exception) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.message)
            session.transactionManager.rollback()
        }
        session.close()
    }

    private fun createMixchangeRealm() {
        val session = getSessionFactory().create()
        try {
            session.transactionManager.begin()
            val manager = RealmManager(session)
            val lessonRealmImportFile: Resource = ClassPathResource(
                keycloakServerProperties!!.realmImportFile
            )
            manager.importRealm(
                JsonSerialization.readValue(
                    lessonRealmImportFile.getInputStream(),
                    RealmRepresentation::class.java
                )
            )
            session.transactionManager.commit()
        } catch (ex: Exception) {
            LOG.warn("Failed to import Realm json file: {}", ex.message)
            session.transactionManager.rollback()
        }
        session.close()
    }
}