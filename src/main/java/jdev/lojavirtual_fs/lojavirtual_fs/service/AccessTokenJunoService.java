package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jdev.lojavirtual_fs.lojavirtual_fs.model.AccessTokenJunoApi;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenJunoService {

    @PersistenceContext
    private EntityManager entityManager;

    public AccessTokenJunoApi buscaTokenAtivo() {
        try {
            AccessTokenJunoApi accessTokenJunoApi =
                    (AccessTokenJunoApi) entityManager.createQuery("select a from AccessTokenJunoApi a")
                            .setMaxResults(1).getSingleResult();
            return accessTokenJunoApi;
        }catch (NoResultException e) {
            return null;
        }

    }
}
