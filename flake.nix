{
  description = "libGDX development shell";

  inputs = {
    flake-utils.url = "github:numtide/flake-utils";
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-25.11";
  };

  outputs = { flake-utils, nixpkgs, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        runtimeLibs = with pkgs;
          lib.makeLibraryPath [
            alsa-lib
            glfw3-minecraft
            libGL
            xorg.libX11
            xorg.libXcursor
            xorg.libXext
            xorg.libXrandr
            xorg.libXxf86vm
          ];
      in {
        devShells.default = pkgs.mkShell {
          packages = with pkgs; [ gradle jdk21 ];

          shellHook = ''
            export LD_LIBRARY_PATH=${runtimeLibs}:$LD_LIBRARY_PATH
            export __GL_THREADED_OPTIMIZATIONS=0 # Fix https://github.com/glfw/glfw/issues/2510
          '';
        };
      });
}
