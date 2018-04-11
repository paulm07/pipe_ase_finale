#define BOUND 6


#define wait_msg(p1) place_wait_msg??[eval(p1)]
#define Cpt2(p1) place_Cpt2??[eval(p1)]
#define S_tout(p1) place_S_tout??[eval(p1)]
#define loop_em(p1) place_loop_em??[eval(p1)]
#define R_out(p1) place_R_out??[eval(p1)]
#define ACK(p1) place_ACK??[eval(p1)]
#define MSG(p1) place_MSG??[eval(p1)]
#define listen(p1) place_listen??[eval(p1)]
#define T_out(p1) place_T_out??[eval(p1)]
#define wait_ack(p1,p2) place_wait_ack??[eval(p1),eval(p2)]
#define wait_cable(p1) place_wait_cable??[eval(p1)]
#define Cpt1(p1) place_Cpt1??[eval(p1)]
#define msg1(p1) place_msg1??[eval(p1)]
#define cable_used(p1) place_cable_used??[eval(p1)]
#define Cable_free(p1) place_Cable_free??[eval(p1)]
#define AMC(p1,p2) place_AMC??[eval(p1),eval(p2)]
#define RMC(p1) place_RMC??[eval(p1)]
#define PMC(p1) place_PMC??[eval(p1)]
#define FMCb(p1) place_FMCb??[eval(p1)]
#define FMC(p1) place_FMC??[eval(p1)]

typedef Type_1 {
  short field1;
};

Type_1 var_Type_1;

typedef Type_2 {
  short field1;
};

Type_2 var_Type_2;

typedef Type_3 {
  short field1;
  short field2;
};

Type_3 var_Type_3;

chan place_wait_msg = [BOUND] of {Type_1};
chan place_Cpt2 = [BOUND] of {Type_1};
chan place_S_tout = [BOUND] of {Type_2};
chan place_loop_em = [BOUND] of {Type_1};
chan place_R_out = [BOUND] of {Type_2};
chan place_ACK = [BOUND] of {Type_2};
chan place_MSG = [BOUND] of {Type_1};
chan place_listen = [BOUND] of {Type_1};
chan place_T_out = [BOUND] of {Type_2};
chan place_wait_ack = [BOUND] of {Type_3};
chan place_wait_cable = [BOUND] of {Type_1};
chan place_Cpt1 = [BOUND] of {Type_1};
chan place_msg1 = [BOUND] of {Type_1};
chan place_cable_used = [BOUND] of {Type_1};
chan place_Cable_free = [BOUND] of {Type_2};
chan place_AMC = [BOUND] of {Type_3};
chan place_RMC = [BOUND] of {Type_1};
chan place_PMC = [BOUND] of {Type_1};
chan place_FMCb = [BOUND] of {Type_2};
chan place_FMC = [BOUND] of {Type_1};

inline clean(source, var) {
  do
  :: nempty(source) -> source?var
  od
}

inline copy(source, destination, var) {
  for (var in source) {
    destination!var;
  }
}

inline transfer(source, dest, var) {
  do
  :: nempty(source) -> source?var; dest!var;
  od
}

int ID_C_refuse = 1;
int ID_C_provide = 2;
int ID_I_ask2 = 4;
int ID_I_refused = 8;
int ID_I_reemit = 16;
int ID_I_emit = 32;
int ID_C_free = 64;
int ID_Gto = 128;
int ID_I_rec2 = 256;
int ID_I_rec1 = 512;
int ID_loss_a = 1024;
int ID_loss_m = 2048;
int ID_I_free = 4096;
int ID_I_ask1 = 8192;
int allEnabled = 16383;
int enabledAny = 16383;
inline disable(setBit) {
  enabledAny = enabledAny&~setBit;
  isEnabledAny = enabledAny>0;
}

inline enableAll() {
  enabledAny = allEnabled;
  isEnabledAny = true;
}

bool isEnabledAny = true;
bool isInitialized = false;
inline is_enabled_C_refuse() {
  for (y in place_Cpt2) {
    for (j in place_cable_used) {
      for (k in place_FMCb) {
        for (ix in place_AMC) {
          if
          :: (y.field1 == ix.field2) -> enableAll(); fire_C_refuse_0(); goto endfire;
          :: else -> skip;
          fi

        }

      }

    }

  }
  disable(ID_C_refuse);
  endfire:
}

inline fire_C_refuse_0() {
  place_Cpt2??eval(y.field1);
  place_cable_used??eval(j.field1);
  place_FMCb??eval(k.field1);
  place_AMC??eval(ix.field1),eval(ix.field2);

  x1.field1 = ix.field2 + 1;
  i.field1 = ix.field1;
;

  place_Cpt2!x1.field1;
  place_RMC!i.field1;
  place_cable_used!j.field1;
  place_FMCb!k.field1;

}
active proctype C_refuse() {
  
  Type_1 y;
  Type_1 x1;
  Type_1 i;
  Type_1 j;
  Type_2 k;
  Type_3 ix;
  
  isInitialized == true;
  start_C_refuse:
  atomic {
    is_enabled_C_refuse();
    if
    :: !isEnabledAny -> goto end_C_refuse;
    :: else -> skip;
    fi
  }
  goto start_C_refuse;
  end_C_refuse:
}

inline is_enabled_C_provide() {
  for (h in place_Cable_free) {
    for (y in place_Cpt2) {
      for (k in place_FMCb) {
        for (ix in place_AMC) {
          if
          :: (y.field1 == ix.field2 && h.field1 == 1) -> enableAll(); fire_C_provide_0(); goto endfire;
          :: else -> skip;
          fi

        }

      }

    }

  }
  disable(ID_C_provide);
  endfire:
}

inline fire_C_provide_0() {
  place_Cable_free??eval(h.field1);
  place_Cpt2??eval(y.field1);
  place_FMCb??eval(k.field1);
  place_AMC??eval(ix.field1),eval(ix.field2);

  x1.field1 = ix.field2 + 1;
  i.field1 = ix.field1;
;

  place_Cpt2!x1.field1;
  place_cable_used!i.field1;
  place_PMC!i.field1;
  place_FMCb!k.field1;

}
active proctype C_provide() {
  
  Type_2 h;
  Type_1 y;
  Type_1 x1;
  Type_1 i;
  Type_2 k;
  Type_3 ix;
  
  isInitialized == true;
  start_C_provide:
  atomic {
    is_enabled_C_provide();
    if
    :: !isEnabledAny -> goto end_C_provide;
    :: else -> skip;
    fi
  }
  goto start_C_provide;
  end_C_provide:
}

inline is_enabled_I_ask2() {
  for (x in place_Cpt1) {
    for (i in place_loop_em) {
      if
      :: (true) -> enableAll(); fire_I_ask2_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_ask2);
  endfire:
}

inline fire_I_ask2_0() {
  place_Cpt1??eval(x.field1);
  place_loop_em??eval(i.field1);

  x1.field1 = x.field1 + 1;
  ix.field1 = i.field1;
  ix.field2 = x.field1;
;

  place_Cpt1!x1.field1;
  place_wait_cable!i.field1;
  place_AMC!ix.field1,ix.field2;

}
active proctype I_ask2() {
  
  Type_1 x;
  Type_1 x1;
  Type_1 i;
  Type_3 ix;
  
  isInitialized == true;
  start_I_ask2:
  atomic {
    is_enabled_I_ask2();
    if
    :: !isEnabledAny -> goto end_I_ask2;
    :: else -> skip;
    fi
  }
  goto start_I_ask2;
  end_I_ask2:
}

inline is_enabled_I_refused() {
  for (i in place_wait_cable) {
    for (j in place_RMC) {
      if
      :: (i.field1 == j.field1) -> enableAll(); fire_I_refused_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_refused);
  endfire:
}

inline fire_I_refused_0() {
  place_wait_cable??eval(i.field1);
  place_RMC??eval(j.field1);
  place_wait_msg!i.field1;

}
active proctype I_refused() {
  
  Type_1 i;
  Type_1 j;
  
  isInitialized == true;
  start_I_refused:
  atomic {
    is_enabled_I_refused();
    if
    :: !isEnabledAny -> goto end_I_refused;
    :: else -> skip;
    fi
  }
  goto start_I_refused;
  end_I_refused:
}

inline is_enabled_I_reemit() {
  for (ir in place_wait_ack) {
    for (k in place_T_out) {
      if
      :: (k.field1 == 1) -> enableAll(); fire_I_reemit_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_reemit);
  endfire:
}

inline fire_I_reemit_0() {
  place_wait_ack??eval(ir.field1),eval(ir.field2);
  place_T_out??eval(k.field1);

  r.field1 = ir.field2;
;

  place_MSG!r.field1;
  place_wait_ack!ir.field1,ir.field2;

}
active proctype I_reemit() {
  
  Type_1 r;
  Type_3 ir;
  Type_2 k;
  
  isInitialized == true;
  start_I_reemit:
  atomic {
    is_enabled_I_reemit();
    if
    :: !isEnabledAny -> goto end_I_reemit;
    :: else -> skip;
    fi
  }
  goto start_I_reemit;
  end_I_reemit:
}

inline is_enabled_I_emit() {
  for (r in place_msg1) {
    for (i in place_wait_cable) {
      for (j in place_PMC) {
        if
        :: (i.field1 == j.field1 && r.field1 != i.field1) -> enableAll(); fire_I_emit_0(); goto endfire;
        :: else -> skip;
        fi

      }

    }

  }
  disable(ID_I_emit);
  endfire:
}

inline fire_I_emit_0() {
  place_msg1??eval(r.field1);
  place_wait_cable??eval(i.field1);
  place_PMC??eval(j.field1);

  ir.field1 = i.field1;
  ir.field2 = r.field1;
;

  place_MSG!r.field1;
  place_msg1!r.field1;
  place_wait_ack!ir.field1,ir.field2;

}
active proctype I_emit() {
  
  Type_1 r;
  Type_1 i;
  Type_3 ir;
  Type_1 j;
  
  isInitialized == true;
  start_I_emit:
  atomic {
    is_enabled_I_emit();
    if
    :: !isEnabledAny -> goto end_I_emit;
    :: else -> skip;
    fi
  }
  goto start_I_emit;
  end_I_emit:
}

inline is_enabled_C_free() {
  for (i in place_cable_used) {
    for (j in place_FMC) {
      if
      :: (i.field1 == j.field1) -> enableAll(); fire_C_free_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_C_free);
  endfire:
}

inline fire_C_free_0() {
  place_cable_used??eval(i.field1);
  place_FMC??eval(j.field1);

  k.field1 = 1;
;

  place_Cable_free!k.field1;
  place_FMCb!k.field1;

}
active proctype C_free() {
  
  Type_1 i;
  Type_1 j;
  Type_2 k;
  
  isInitialized == true;
  start_C_free:
  atomic {
    is_enabled_C_free();
    if
    :: !isEnabledAny -> goto end_C_free;
    :: else -> skip;
    fi
  }
  goto start_C_free;
  end_C_free:
}

inline is_enabled_Gto() {
  for (i in place_FMC) {
    for (k in place_S_tout) {
      if
      :: (true) -> enableAll(); fire_Gto_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_Gto);
  endfire:
}

inline fire_Gto_0() {
  place_FMC??eval(i.field1);
  place_S_tout??eval(k.field1);
  place_FMC!i.field1;
  place_R_out!k.field1;

}
active proctype Gto() {
  
  Type_1 i;
  Type_2 k;
  
  isInitialized == true;
  start_Gto:
  atomic {
    is_enabled_Gto();
    if
    :: !isEnabledAny -> goto end_Gto;
    :: else -> skip;
    fi
  }
  goto start_Gto;
  end_Gto:
}

inline is_enabled_I_rec2() {
  for (r in place_MSG) {
    for (i in place_wait_msg) {
      if
      :: (r.field1 == i.field1) -> enableAll(); fire_I_rec2_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_rec2);
  endfire:
}

inline fire_I_rec2_0() {
  place_MSG??eval(r.field1);
  place_wait_msg??eval(i.field1);

  h.field1 = 1;
;

  place_ACK!h.field1;
  place_loop_em!i.field1;

}
active proctype I_rec2() {
  
  Type_1 r;
  Type_2 h;
  Type_1 i;
  
  isInitialized == true;
  start_I_rec2:
  atomic {
    is_enabled_I_rec2();
    if
    :: !isEnabledAny -> goto end_I_rec2;
    :: else -> skip;
    fi
  }
  goto start_I_rec2;
  end_I_rec2:
}

inline is_enabled_I_rec1() {
  for (r in place_MSG) {
    for (i in place_listen) {
      if
      :: (r.field1 == i.field1) -> enableAll(); fire_I_rec1_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_rec1);
  endfire:
}

inline fire_I_rec1_0() {
  place_MSG??eval(r.field1);
  place_listen??eval(i.field1);

  h.field1 = 1;
;

  place_ACK!h.field1;
  place_listen!i.field1;

}
active proctype I_rec1() {
  
  Type_1 r;
  Type_2 h;
  Type_1 i;
  
  isInitialized == true;
  start_I_rec1:
  atomic {
    is_enabled_I_rec1();
    if
    :: !isEnabledAny -> goto end_I_rec1;
    :: else -> skip;
    fi
  }
  goto start_I_rec1;
  end_I_rec1:
}

inline is_enabled_loss_a() {
  for (h in place_ACK) {
    for (k in place_R_out) {
      if
      :: (h.field1 == 1) -> enableAll(); fire_loss_a_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_loss_a);
  endfire:
}

inline fire_loss_a_0() {
  place_ACK??eval(h.field1);
  place_R_out??eval(k.field1);
  place_S_tout!k.field1;
  place_T_out!k.field1;

}
active proctype loss_a() {
  
  Type_2 h;
  Type_2 k;
  
  isInitialized == true;
  start_loss_a:
  atomic {
    is_enabled_loss_a();
    if
    :: !isEnabledAny -> goto end_loss_a;
    :: else -> skip;
    fi
  }
  goto start_loss_a;
  end_loss_a:
}

inline is_enabled_loss_m() {
  for (r in place_MSG) {
    for (k in place_R_out) {
      if
      :: (r.field1 > 0) -> enableAll(); fire_loss_m_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_loss_m);
  endfire:
}

inline fire_loss_m_0() {
  place_MSG??eval(r.field1);
  place_R_out??eval(k.field1);
  place_S_tout!k.field1;
  place_T_out!k.field1;

}
active proctype loss_m() {
  
  Type_1 r;
  Type_2 k;
  
  isInitialized == true;
  start_loss_m:
  atomic {
    is_enabled_loss_m();
    if
    :: !isEnabledAny -> goto end_loss_m;
    :: else -> skip;
    fi
  }
  goto start_loss_m;
  end_loss_m:
}

inline is_enabled_I_free() {
  for (h in place_ACK) {
    for (ir in place_wait_ack) {
      for (k in place_FMCb) {
        if
        :: (h.field1 == 1 && k.field1 == 1) -> enableAll(); fire_I_free_0(); goto endfire;
        :: else -> skip;
        fi

      }

    }

  }
  disable(ID_I_free);
  endfire:
}

inline fire_I_free_0() {
  place_ACK??eval(h.field1);
  place_wait_ack??eval(ir.field1),eval(ir.field2);
  place_FMCb??eval(k.field1);

  i.field1 = ir.field1;
;

  place_listen!i.field1;
  place_FMC!i.field1;

}
active proctype I_free() {
  
  Type_2 h;
  Type_1 i;
  Type_3 ir;
  Type_2 k;
  
  isInitialized == true;
  start_I_free:
  atomic {
    is_enabled_I_free();
    if
    :: !isEnabledAny -> goto end_I_free;
    :: else -> skip;
    fi
  }
  goto start_I_free;
  end_I_free:
}

inline is_enabled_I_ask1() {
  for (x in place_Cpt1) {
    for (i in place_listen) {
      if
      :: (true) -> enableAll(); fire_I_ask1_0(); goto endfire;
      :: else -> skip;
      fi

    }

  }
  disable(ID_I_ask1);
  endfire:
}

inline fire_I_ask1_0() {
  place_Cpt1??eval(x.field1);
  place_listen??eval(i.field1);

  x1.field1 = x.field1 + 1;
  ix.field1 = i.field1;
  ix.field2 = x.field1;
;

  place_Cpt1!x1.field1;
  place_wait_cable!i.field1;
  place_AMC!ix.field1,ix.field2;

}
active proctype I_ask1() {
  
  Type_1 x;
  Type_1 x1;
  Type_1 i;
  Type_3 ix;
  
  isInitialized == true;
  start_I_ask1:
  atomic {
    is_enabled_I_ask1();
    if
    :: !isEnabledAny -> goto end_I_ask1;
    :: else -> skip;
    fi
  }
  goto start_I_ask1;
  end_I_ask1:
}

proctype Main() {
  printf("main");
}

init {

  place_Cpt2!1;



  place_R_out!1;



  place_listen!1;
  place_listen!2;
  place_listen!3;




  place_Cpt1!1;

  place_msg1!1;
  place_msg1!2;
  place_msg1!3;


  place_Cable_free!1;




  place_FMCb!1;


  isInitialized = true;
  run Main()
}



int x;
int k;
ltl f1 { []!(len(place_cable_used)>1) }
ltl f2 { []!(RMC(x) && PMC(x)) }
ltl f3 {  [](listen(k) -><>cable_used(k)) }

