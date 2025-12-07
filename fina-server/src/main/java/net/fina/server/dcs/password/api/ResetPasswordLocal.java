package net.fina.server.dcs.password.api;

import net.fina.common.client.exception.FinATypeException;

import java.io.Serializable;


public interface ResetPasswordLocal extends Serializable {
    void resetPasswordByEmail(String login, String mail, String langCode) throws FinATypeException;

    void resetPasswordBySms(String login, String phone) throws FinATypeException;

    void resetPasswordByEmailAndSms(String login, String mail, String phone, String langCode) throws FinATypeException;
}
