package com.demo.butterknife_compiler;

import com.demo.butterknife_annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

//@AutoService(Processor.class)
public class ButterKnifeProcessor  extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;
    private Types typeUtils;

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set=new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        System.out.println(FieldViewBinding.class.getName());
        System.out.println(FieldViewBinding.class.getCanonicalName());
        System.out.println(FieldViewBinding.class.getSimpleName());
        System.out.println(FieldViewBinding.class.getTypeName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {

        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 通过roundEnvironment扫描所有的类文件，获取所有存在指定注解的字段
        Map<TypeElement, List<FieldViewBinding>> targetMap = getTargetMap(roundEnv);



        createJavaFile(targetMap.entrySet());
        return false;

    }

    private Map<TypeElement, List<FieldViewBinding>> getTargetMap(RoundEnvironment roundEnvironment) {
        /**
         * 键：TypeElement，指定Activity；
         * 值：List<FieldViewBinding>，activiyt中所有的注解修饰的字段
         */
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();

        // 1、获取代码中所有使用@BindView注解修饰的字段
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : annotatedElements) {

            messager.printMessage(Diagnostic.Kind.NOTE,FieldViewBinding.class.getName());
            // 获取字段名称 (textView)
            String fieldName = element.getSimpleName().toString();
            // 获取字段类型 (android.widget.TextView)
            TypeMirror fieldType = element.asType();
            // 获取注解元素的值 (R.id.textView)
            int viewId = element.getAnnotation(BindView.class).value();

            // 获取声明element的全限定类名 (com.zhangke.simplifybutterknife.MainActivity)
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(typeElement);
            if (list == null) {
                list = new ArrayList<>();
                targetMap.put(typeElement, list);
            }

            list.add(new FieldViewBinding(fieldName, fieldType, viewId));

        }

        return targetMap;
    }

    /**
     * 创建Java文件
     * @param entries
     */
    private void createJavaFile(Set<Map.Entry<TypeElement, List<FieldViewBinding>>> entries) {
        for (Map.Entry<TypeElement, List<FieldViewBinding>> entry : entries) {
            TypeElement typeElement = entry.getKey();
            List<FieldViewBinding> list = entry.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }


            //elementUtils.getPackageOf(typeElement)

            // 获取包名
            String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            System.out.println("packageName======"+packageName);
            // 根据旧Java类名创建新的Java文件
            String className = typeElement.getQualifiedName().toString().substring(packageName.length() + 1);
            String newClassName = className + "_ViewBinding";


            //MethodSpec.Builder

            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(className), "target");
            for (FieldViewBinding fieldViewBinding : list) {
                String packageNameString = fieldViewBinding.getFieldType().toString();
                messager.printMessage(Diagnostic.Kind.NOTE,"packageNameString==="+packageNameString);
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                methodBuilder.addStatement
                        ("target.$L=($T)target.findViewById($L)", fieldViewBinding.getFieldName()
                                , viewClass, fieldViewBinding.getViewId());
            }


            TypeSpec typeBuilder = TypeSpec.classBuilder(newClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();


            JavaFile javaFile = JavaFile.builder(packageName, typeBuilder)
                    .addFileComment("Generated code from Butter Knife. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
