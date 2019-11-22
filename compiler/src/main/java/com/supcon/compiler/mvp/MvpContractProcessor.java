package com.supcon.compiler.mvp;

import com.google.auto.service.AutoService;
import com.supcon.annotation.contract.mvp.MvpContractFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;


// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理（新增annotation module）
@SupportedAnnotationTypes({"com.supcon.annotation.contract.mvp.MvpContractFactory"})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MvpContractProcessor extends AbstractProcessor {


    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的源文件，class文件以及辅助文件
    private Filer filer;

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类


    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 父类受保护属性，可以直接拿来使用。
        // 其实就是init方法的参数ProcessingEnvironment
        // processingEnv.getMessager(); //参考源码64行
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) return false;

        // 获取所有带ARouter注解的 类节点
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(MvpContractFactory.class);
        // 遍历所有类节点
        for (Element element : elements) {
            // 通过类节点获取包节点（全路径：com.netease.xxx）
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取简单类名
            String className = element.getSimpleName().toString();
            // 最终想生成的类文件名
            String finalClassName = className + "MvpContract";

            // 公开课写法，也是EventBus写法（https://github.com/greenrobot/EventBus）
            try {
                // 创建一个新的源文件（Class），并返回一个对象以允许写入它
                JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + finalClassName);
                // 定义Writer对象，开启写入
                Writer writer = sourceFile.openWriter();

                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("package " + packageName + ";\n")
                        .append("public interface " + finalClassName + " {\n");

                List<? extends Element> methodElements = element.getEnclosedElements();
                for (Element methodElement : methodElements) {
                    if (methodElement instanceof ExecutableElement) {
                        TypeMirror returnType = ((ExecutableElement) methodElement).getReturnType();
                        Name methodElementSimpleName = methodElement.getSimpleName();
                        strBuilder.append(returnType + " " + methodElementSimpleName + "(");
                        List<? extends VariableElement> parameters = ((ExecutableElement) methodElement).getParameters();
                        for (VariableElement parameter : parameters) {
                            Name simpleName = parameter.getSimpleName();
                            Element enclosingElement = parameter.getEnclosingElement();
                            TypeMirror typeMirror = enclosingElement.asType();
                            strBuilder.append(simpleName1 + " " + simpleName + ");\n");
                        }

                    }
                }
                strBuilder.append("}");
                writer.write(strBuilder.toString());
                // 最后结束别忘了
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void createFile(Writer writer) {


    }
}
