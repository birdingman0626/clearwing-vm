#include "java/lang/ref/WeakReference.h"

jobject M_java_lang_ref_WeakReference_get_R_java_lang_Object(jcontext ctx, jobject self) {
    return (jobject)((jweak)self)->F_ptr;
}

void M_java_lang_ref_WeakReference_clear(jcontext ctx, jobject self) {
    auto weak = (jweak)self;
    if (!weak->F_ptr)
      return;
    deregisterWeak(weak);
    weak->F_ptr = 0;
}

void M_java_lang_ref_WeakReference_init_java_lang_Object(jcontext ctx, jobject self, jobject ref) {
    ((jweak)self)->F_ptr = (jlong)ref;
}

void M_java_lang_ref_WeakReference_finalize(jcontext ctx, jobject self) {
    auto weak = (jweak)self;
    if (weak->F_ptr)
        deregisterWeak(weak);
}
