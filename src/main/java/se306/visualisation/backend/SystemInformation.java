//package se306.visualisation.backend;
//
//public class SystemInformation {
//	public static native long getProcessCPUTime();
//
//	public static final class CPUUsageSnapshot {
//		private CPUUsageSnapshot (long time, long CPUTime)
//		{
//			m_time = time;
//			m_CPUTime = CPUTime;
//		}
//
//		public final long m_time, m_CPUTime;
//
//	}
//
//	public static CPUUsageSnapshot makeCPUUsageSnapshot () {
//		return new CPUUsageSnapshot (System.currentTimeMillis (), getProcessCPUTime ());
//	}
//
//	public static double getProcessCPUUsage (CPUUsageSnapshot start, CPUUsageSnapshot end) {
//		return ((double)(end.m_CPUTime - start.m_CPUTime)) / (end.m_time - start.m_time);
//	}
//
//	private static final String SILIB = "silib";
//
//	static {
//		try
//		{
//			System.loadLibrary (SILIB);
//		}
//		catch (UnsatisfiedLinkError e)
//		{
//			System.out.println ("native lib '" + SILIB + "' not found in 'java.library.path': "
//					+ System.getProperty ("java.library.path"));
//
//			throw e; // re-throw
//		}
//	}
//}
