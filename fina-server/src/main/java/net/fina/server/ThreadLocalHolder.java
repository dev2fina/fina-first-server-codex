package net.fina.server;

import net.fina.common.shared.LanguageSampleModel;

import jakarta.servlet.http.HttpServletRequest;

public class ThreadLocalHolder {
    private static final  ThreadLocal<LanguageSampleModel> languageThreadLocal = new ThreadLocal<>();
    private static final  ThreadLocal<HttpServletRequest> perThreadRequest = new ThreadLocal<>();

    public static void set(LanguageSampleModel language,HttpServletRequest request){
        setLanguage(language);
        setThreadLocalRequest(request);
    }

    private static void setLanguage(LanguageSampleModel language) {
        languageThreadLocal.set(language);
    }

    public static LanguageSampleModel getLanguage() {
        return languageThreadLocal.get();
    }

    private static void setThreadLocalRequest(HttpServletRequest request){
        perThreadRequest.set(request);
    }
    public static HttpServletRequest getThreadLocalRequest(){
        return perThreadRequest.get();
    }

    public static void remove() {
        languageThreadLocal.remove();
        perThreadRequest.remove();
    }


}
