package ic.doc.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

  private static final byte[] IMAGE = new byte[4];

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  Sensor sensor = context.mock(Sensor.class);
  MemoryCard memoryCard = context.mock(MemoryCard.class);

  Camera camera = new Camera(sensor, memoryCard);

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {
    context.checking(new Expectations(){{
      exactly(1).of(sensor).powerUp();
                     }});
    camera.powerOn();
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {
    switchOnCamera();

    context.checking(new Expectations(){{
      exactly(1).of(sensor).powerDown();
    }});
    camera.powerOff();
  }



  @Test
  public void pressingTheShutterWithPowerOnCopiesDataFromSensorToMemoryCard() {

    switchOnCamera();

    context.checking(new Expectations(){{
      ignoring(sensor).powerUp();
      exactly(1).of(sensor).readData(); will(returnValue(IMAGE));
      exactly(1).of(memoryCard).write(IMAGE);
    }});
    camera.pressShutter();
  }

  @Test
  public void pressingTheShutterWithPowerOffDoesNothing() {
    context.checking(new Expectations(){{
      never(sensor);
      never(memoryCard);
    }});
    camera.pressShutter();
  }

  @Test
  public void doesNotPowerDownSensorUntilWritingIsComplete() {
    switchOnCamera();
    context.checking(new Expectations(){{
      exactly(1).of(sensor).readData(); will(returnValue(IMAGE));
      exactly(1).of(memoryCard).write(IMAGE);
    }});
    camera.powerOn();
    camera.pressShutter();

    camera.powerOff();

    context.checking(new Expectations() {{
      exactly(1).of(sensor).powerDown();
    }});

    camera.writeComplete();
  }

  private void switchOnCamera() {
    context.checking(new Expectations(){{
      ignoring(sensor).powerUp();
    }});
    camera.powerOn();
  }


}
