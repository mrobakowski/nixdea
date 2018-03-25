{lib, pkgs}:
let inherit (lib) nv nvs; in
{

  composableDerivation = {
        mkDerivation ? pkgs.stdenv.mkDerivation,

        applyPreTidy ? [ lib.prepareDerivationArgs ],

        removeAttrs ? ["cfg" "flags"]

      }: (lib.defaultOverridableDelayableArgs ( a: mkDerivation a)
         {
           inherit applyPreTidy removeAttrs;
         }).merge;

  edf = {name, feat ? name, enable ? {}, disable ? {} , value ? ""}:
    nvs name {
    set = {
      configureFlags = ["--enable-${feat}${if value == "" then "" else "="}${value}"];
    } // enable;
    unset = {
      configureFlags = ["--disable-${feat}"];
    } // disable;
  };

  wwf = {name, feat ? name, enable ? {}, disable ? {}, value ? ""}:
    nvs name {
    set = enable // {
      configureFlags = ["--with-${feat}${if value == "" then "" else "="}${value}"]
                       ++ lib.maybeAttr "configureFlags" [] enable;
    };
    unset = disable // {
      configureFlags = ["--without-${feat}"]
                       ++ lib.maybeAttr "configureFlags" [] disable;
    };
  };
}
