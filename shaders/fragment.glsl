// ==========================================================================
// Vertex program for barebones GLFW boilerplate
//
// Author:  Sonny Chan, University of Calgary
// Date:    December 2015
// ==========================================================================
#version 410
// uniform varible for our image:
// uniform for the color multipliers for luminousity
// this matrix is used to adjust colors, for example:
// | .333 .333 .333 |
// | .333 .333 .333 |
// | .333 .333 .333 | 
// would result in a grey scale, where as
// | 1 0 0 |
// | 0 1 0 |
// | 0 0 1 |
// would output the normal rgb values
// interpolated colour received from vertex stage
in vec3 color;
// first output is mapped to the framebuffer's colour index by default
out vec4 FragmentColour;


void main(void)
{
    // write colour output without modification

    FragmentColour = vec4(color,1);
}
