# s3mInstaller

Installer for the java semistructured 3-way merge tool.

<h2> How to update the installer jar</h2>

<p>When you generate the project jar file, you can update it by overwriting the s3m.jar file in the "files" folder with the following two methods:</p>

<ul>
  <h3> I -Jar update command</h3>
  <li>
      <ul>
        <li><code>jar uf jar_installer.jar input-file</code></li>
        <li> more info about this method in <a href="https://docs.oracle.com/javase/tutorial/deployment /jar/update.html"> Jar Documentation</a></li>
      </ul>
   </li>
   <li>
   <h3> II - Zip method</h3>
      <ul>
      <li>A jar file is basically a zip file, so you can use zip tools to open and overwrite its contents</li>
      </ul>
</ul>   
