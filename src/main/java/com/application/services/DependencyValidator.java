package com.application.services;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.application.entities.Dependency;
import  com.application.services.DependencyValidator.DependencyValidatorResult;
import  static com.application.services.DependencyValidator.DependencyValidatorResult.NAME_NOT_VALID;
import static com.application.services.DependencyValidator.DependencyValidatorResult.SUCCESS;
import static com.application.services.DependencyValidator.DependencyValidatorResult.VERSION_NOT_VALID;

public interface DependencyValidator extends Function<Dependency, DependencyValidatorResult> {
    

    static DependencyValidator validateName(){
        return dependency -> {
            Pattern pattern = Pattern.compile( "[~^(){}#=+&\'\\:;,?]", Pattern.CASE_INSENSITIVE );
            Matcher matcher = pattern.matcher(dependency.getName());
            return dependency.getName() == null
                        || matcher.find() ?  
                            NAME_NOT_VALID : SUCCESS ;
        } ;
    }

    static DependencyValidator validateVersion() {
        return dependency -> {

            Pattern pattern = Pattern.compile( "[~^(){}#=+&\'\\/:;,?]", Pattern.CASE_INSENSITIVE );
            Matcher matcher = pattern.matcher(dependency.getVersion());
            
            return dependency.getName() == null 
                        || matcher.find() ?  
                            VERSION_NOT_VALID : SUCCESS;
            
        } ;
    }

    default DependencyValidator and (DependencyValidator other) {
        return dependency -> {
            DependencyValidatorResult result = this.apply(dependency);

            return result.equals(SUCCESS) ? other.apply(dependency) : result;
        };
    }


    enum DependencyValidatorResult {

        NAME_NOT_VALID("the name of this dependency is not valid"),
        VERSION_NOT_VALID("the version is not valid"),
        SUCCESS("right, all is good");

        private String message;

        DependencyValidatorResult(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
