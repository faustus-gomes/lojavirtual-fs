package jdev.lojavirtual_fs.lojavirtual_fs.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "access_token_junoapi")
@SequenceGenerator(name = "seq_access_token_junoapi", sequenceName = "seq_access_token_junoapi", allocationSize = 1, initialValue = 1)
public class AccessTokenJunoApi implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_access_token_junoapi")
    private long id;
    @Column(columnDefinition = "text")
    private String access_token;
    private String expires_in;
    private String scope;
    private String user_name;
    private String jti;
    private String token_acesso;
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = Calendar.getInstance().getTime();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getToken_acesso() {
        return token_acesso;
    }

    public void setToken_acesso(String token_acesso) {
        this.token_acesso = token_acesso;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    public boolean expirado() {
        Date dataAtual = Calendar.getInstance().getTime();
        Long tempo = dataAtual.getTime() - this.dataCadastro.getTime(); /*Tempo entre datas*/
        Long minutos = (tempo / 1000 ) / 60; /*diferença de minutos entre datas e horas inicial e */

        if (minutos.intValue() > 50) {
            return true;
        }
        return false;
    }
}
