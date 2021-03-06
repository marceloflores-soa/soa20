package service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import datacontractImpl.LoginResponse;
import domain.LoginUser;
import interfaces.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.APIUtils;

/**
 * Servicio que corre en background debido a que extiende de IntentService, se utiliza para loguear
 * los eventos de login
 */
public class LoginService extends IntentService {

    private LoginUser user;
    private APIService soaService;

    //Ambientes
    private static final String ENV_PROD = "PROD";
    private static final String ENV_TST = "TEST";

    //Action a responser al broadcast
    public static final String LOGIN_OK = "service.LOGIN_OK";
    public static final String LOGIN_ERROR = "service.LOGIN_ERROR";
    public static final String LOGIN_FAIL_CALL = "service.LOGIN_FAIL_CALL";


    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Bundle bundle = intent.getExtras();
        user = (LoginUser) bundle.get("User");
        String action = bundle.getString("action");

        //Retrofit

        soaService = APIUtils.getAPIService();

        soaService.login(user).enqueue(loginCallBack());

    }

    //Callback de la llamada al Login
    public Callback loginCallBack() {
        return new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Intent intentLogin = new Intent();
                if(response.isSuccessful()){
                    intentLogin.setAction(LOGIN_OK);
                    intentLogin.putExtra("token", response.body().getToken());
                } else {
                    intentLogin.setAction(LOGIN_ERROR);
                    intentLogin.putExtra("msgError", "Error de autenticación");
                }
                //Retorno respuesta al activity que llamo al servicio
                sendBroadcast(intentLogin);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Intent intentLogin = new Intent();
                intentLogin.setAction(LOGIN_FAIL_CALL);
                intentLogin.putExtra("msgError", "Error al comunicarse con el servicio de autenticación");
                //Retorno respuesta al activity que llamo al servicio
                sendBroadcast(intentLogin);
            }
        };
    }

    /**
    public Callback registerCallBack() {
        return new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                Intent intentRegister = new Intent();
                if(response.isSuccessful()){
                    intentRegister.setAction(REGISTER_OK);
                } else {
                    intentRegister.setAction(REGISTER_ERROR);
                    intentRegister.putExtra("msgError", "Error en la registración del usuario." +
                            "Favor de revisar que se hayan completado todos los campos correctamente.");
                }
                //Retorno respuesta al activity que llamo al servicio
                sendBroadcast(intentRegister);
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Intent intentLogin = new Intent();
                intentLogin.setAction(REGISTER_FAIL_CALL);
                intentLogin.putExtra("msgError", "Error al comunicarse con el servicio de registración");
                //Retorno respuesta al activity que llamo al servicio
                sendBroadcast(intentLogin);
            }

        };
    } */

}
