package com.shared.infrastructure;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.ExecutionContext;
import com.shared.security.AuthMiddleware;
import com.shared.security.exception.AuthenticationException;
import com.shared.utils.ResponseFactory;
import com.shared.utils.RequestHelper;

public abstract class BaseFunction {

    protected <T, R> HttpResponseMessage execute(
            HttpRequestMessage<T> request,
            ExecutionContext context,
            FunctionAction<T, R> action,
            AuthRequirement auth) {
        
        try {
            if (auth == AuthRequirement.REQUIRED) {
                AuthMiddleware.authenticate(request);
            }
            
            R result = action.execute(request.getBody());
            return ResponseFactory.success(request, result);
                    
        } catch (AuthenticationException e) {
            return ResponseFactory.unauthorized(request, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseFactory.badRequest(request, e.getMessage());
        } catch (FunctionExecutionException e) {
            context.getLogger().severe(String.format(
                "Error en función %s: %s. Input: %s", 
                e.getFunctionName(), 
                e.getMessage(), 
                e.getInput()
            ));
            return ResponseFactory.serverError(request, "Error procesando la solicitud");
        } catch (Exception e) {
            context.getLogger().severe("Error: " + e.getMessage());
            return ResponseFactory.serverError(request, e.getMessage());
        }
    }

    protected int getPage(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "page", 0);
    }

    protected int getSize(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "size", 10);
    }

    @FunctionalInterface
    protected interface FunctionAction<T, R> {
        R execute(T body) throws FunctionExecutionException;
    }

    protected enum AuthRequirement {
        REQUIRED, OPTIONAL
    }
}