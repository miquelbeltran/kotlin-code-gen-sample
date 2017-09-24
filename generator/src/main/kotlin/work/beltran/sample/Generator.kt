package work.beltran.sample

import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class Generator: AbstractProcessor() {

    private val kaptKotlinGeneratedOption = "kapt.kotlin.generated"
    private lateinit var kaptKotlinGenerated: File

    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
        kaptKotlinGenerated = File(processingEnv.options[kaptKotlinGeneratedOption])
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedAnnotationTypes")
        return mutableSetOf(GenName::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("process")
        roundEnv.getElementsAnnotatedWith(GenName::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    println("Processing: $className")
                    val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                    generateClass(className, pack)
                }
        return true
    }

    private fun generateClass(className: String, `package`: String) {
        val source = generateSource(`package`, className)
        println("Generated: $source")
        val relativePath = `package`.replace('.', File.separatorChar)
        val folder = File(kaptKotlinGenerated, relativePath).apply { mkdirs() }
        File(folder, "Generated_$className.kt").writeText(source)
        println("Storing on: $folder")
    }

    private fun generateSource(pack: String, className: String) =
            """
        package $pack

        class Generated_$className() {
            fun getName() = "World"
        }
        """.trimIndent()
}